/*
 * Copyright (c) 2014 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial API and implementation
 */
package coyote.cli.actions;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import coyote.commons.StringUtil;
import coyote.dataframe.DataFrame;
import coyote.dataframe.marshal.JSONMarshaler;
import coyote.snow.worker.Response;


public abstract class RestAction extends AbstractAction {

  /** System property which specifies the user name for the proxy server */
  public static final String PROXY_USER = "http.proxyUser";

  /** System property which specifies the user password for the proxy server */
  public static final String PROXY_PASS = "http.proxyPassword";

  /** System property which specifies the proxy server host name */
  public static final String PROXY_HOST = "http.proxyHost";

  /** System property which specifies the port on which proxy server listens */
  public static final String PROXY_PORT = "http.proxyPort";

  /** System property which specifies the NTLM domain for proxy user auth */
  public static final String PROXY_DOMAIN = "http.proxyDomain";

  /** This is the persistent http client we will use to send all our requests */
  private final CloseableHttpClient httpClient = HttpClientBuilder.create().build();

  /** Create a context in which we will execute our request */
  private final HttpClientContext localContext = HttpClientContext.create();

  /** HTTP Client configuration settings */
  protected RequestConfig config = RequestConfig.custom().build();

  /** Holds credentials to proxies and hosts along with different auth schemes */
  final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();




  public RestAction() {
    // retrieve proxy related system properties
    final String proxyhost = System.getProperty( PROXY_HOST );
    final String proxyport = System.getProperty( PROXY_PORT );
    final String proxyuser = System.getProperty( PROXY_USER );
    final String proxypass = System.getProperty( PROXY_PASS );
    final String proxydomain = System.getProperty( PROXY_DOMAIN );

    // If there are both proxy host and port values, configure the request
    // to use them
    if ( StringUtil.isNotBlank( proxyhost ) && StringUtil.isNotBlank( proxyport ) ) {
      final HttpHost proxy = new HttpHost( proxyhost, Integer.parseInt( proxyport ) );
      config = RequestConfig.custom().setProxy( proxy ).build();

      // If we have proxy credentials, add them to the credentials provider
      if ( StringUtil.isNotBlank( proxyuser ) && StringUtil.isNotBlank( proxypass ) ) {

        // if there is a proxy domain, assume NTLM proxy
        if ( StringUtil.isNotBlank( proxydomain ) ) {
          credentialsProvider.setCredentials( new AuthScope( proxyhost, Integer.parseInt( proxyport ), null, "NTLM" ), new NTCredentials( proxyuser, proxypass, null, proxydomain ) );
        } else {
          // otherwise regular proxy authentication
          credentialsProvider.setCredentials( new AuthScope( proxyhost, Integer.parseInt( proxyport ) ), new UsernamePasswordCredentials( proxyuser, proxypass ) );
        } // proxy domain - NTLM

      } // proxy credentials

    } // proxy host & port

    // place the credentials provider in the client context
    localContext.setCredentialsProvider( credentialsProvider );
  }




  /**
   * Execute the request over the current connection and return the results.
   * 
   * <p>Only one record is expected to be returned.</p>
   * 
   * <p>This method makes all calls uniform in nature.</p>
   * 
   * @param request
   * 
   * @return a dataframe containing the results
   * @throws IOException 
   */
  protected Response execute( HttpHost target, final HttpRequest request ) throws IOException {
    final Response response = new Response( request );

    // Set the content type to JSON
    request.setHeader( "content-type", "application/json" );
    request.setHeader( "accept", "application/json" );

    // Execute the request
    try (CloseableHttpResponse httpResponse = httpClient.execute( target, request, localContext )) {

      final int status = httpResponse.getStatusLine().getStatusCode();
      response.setStatusCode( status );
      response.setStatusPhrase( httpResponse.getStatusLine().getReasonPhrase() );
      debug( String.format( "Request:\r\n    %s\r\nResponse:\r\n    %s", request.toString(), httpResponse.getStatusLine().toString() ) );

      // Check for a body
      if ( httpResponse.getEntity() != null ) {

        // get the body as a string
        final String body = EntityUtils.toString( httpResponse.getEntity(), "UTF-8" );

        // Parse the body into frames
        final List<DataFrame> frames = JSONMarshaler.marshal( body );

        // Retrieve the first frame
        if ( frames.size() > 0 ) {
          if ( frames.size() > 1 ) {
            error( "The response contained more than one object - only using first response object" );
          }
          response.setResult( frames.get( 0 ) );

        } // if there are frames parsed from the body

      } // if there is a response entity

    } // try block

    return response;
  }

}
