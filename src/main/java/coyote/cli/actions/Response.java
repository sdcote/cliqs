/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.cli.actions;

import org.apache.http.HttpRequest;

import coyote.dataframe.DataFrame;


/**
 * Used to carry data relating to the response of the requests we make
 * 
 * 200  Success - Success with response body.
 * 201  Created - Success with response body.
 * 204  Success - Success with no response body.
 * 400  Bad Request - The request URI does not match the APIs in the system, 
 *   or the operation failed for unknown reasons. Invalid headers can also 
 *   cause this error.
 * 401  Unauthorized - The user is not authorized to use the API.
 * 403  Forbidden - The requested operation is not permitted for the user. 
 *   This error can also be caused by ACL failures, or business rule or data 
 *   policy constraints.
 * 404  Not found - The requested resource was not found. This can be caused 
 *   by an ACL constraint or if the resource does not exist.
 * 405  Method not allowed - The HTTP action is not allowed for the requested 
 *   REST API, or it is not supported by any API.
 */
public class Response {
  private HttpRequest request = null;
  private int httpStatusCode = 0;
  private String httpStatusPhrase = null;
  private DataFrame result = null;
  private DataFrame errorFrame = null;
  private String status = null;

  /** multi-purpose attribute normally used with 300 series errors containing alink to the redirected location */
  private String link = null;




  /**
   * @param request
   */
  public Response( final HttpRequest request ) {
    this.request = request;
  }




  /**
   * @return the dataframe containing the error results if present
   */
  public DataFrame getErrorFrame() {
    return errorFrame;
  }




  /**
   * @return the HTTP status code (the 200 part of "200 OK")
   */
  public int getHttpStatusCode() {
    return httpStatusCode;
  }




  /**
   * @return the HTTP status phrase  (the "OK" part of "200 OK")
   */
  public String getHttpStatusPhrase() {
    return httpStatusPhrase;
  }




  /**
   * @return the request
   */
  public HttpRequest getRequest() {
    return request;
  }




  public DataFrame getResult() {
    return result;
  }




  /**
   * @return the status
   */
  public String getStatus() {
    return status;
  }




  /**
   * @param frame
   */
  public void setErrorFrame( final DataFrame frame ) {
    errorFrame = frame;
  }




  /**
   * @param frame
   */
  public void setResult( final DataFrame frame ) {
    result = frame;
  }




  /**
   * @param message
   */
  public void setStatus( final String message ) {
    status = message;
  }




  /**
   * @param status
   */
  public void setStatusCode( final int status ) {
    httpStatusCode = status;
  }




  /**
   * @param phrase
   */
  public void setStatusPhrase( final String phrase ) {
    httpStatusPhrase = phrase;
  }




  /**
   * Some responses (e.g. 301, 302) contain a link to the location of where the 
   * request should go for the requested resource.
   * 
   * <p>Not all responses will contain a link. The most common scenario is when 
   * the status code is in the 300 series.</p>
   * 
   * @return the link set in this response
   */
  public String getLink() {
    return link;
  }




  /**
   * @param link the link to set
   */
  public void setLink( String link ) {
    this.link = link;
  }




  /**
   * Informational - Request received, continuing process.
   * @return true if the status code is in the 1XX range
   */
  public boolean isInformational() {
    return ( httpStatusCode < 200 );
  }




  /**
   * Success - The action requested by the client was received, understood, 
   * accepted and processed successfully.
   * @return true if the status code is in the 2XX range
   */
  public boolean isSuccessful() {
    return ( httpStatusCode >= 200 && httpStatusCode < 300 );
  }




  /**
   * Redirection - The client must take additional action to complete the 
   * request.
   * @return true if the status code is in the 3XX range
   */
  public boolean isRedirect() {
    return ( httpStatusCode >= 300 && httpStatusCode < 400 );
  }




  /**
   * Error - Either client or server error.
   * @return true if the status code is greater than or equal to 400.
   */
  public boolean isError() {
    return ( httpStatusCode >= 400 );
  }




  /**
   * Client Error- Indicate cases in which the client seems to have erred.
   * @return true if the status code is in the 4XX range
   */
  public boolean isClientError() {
    return ( httpStatusCode >= 400 && httpStatusCode < 500 );
  }




  /**
   * Server Error - The server failed to fulfill an apparently valid request.
   * @return true if the status code is in the 5XX range
   */
  public boolean isServerError() {
    return ( httpStatusCode >= 500 );
  }

}
