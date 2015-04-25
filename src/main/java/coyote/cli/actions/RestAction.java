package coyote.cli.actions;

import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

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

	public RestAction() {
	}

	/**
	 * This performs a basic HTTP GET for the given URI
	 * 
	 * @param scheme the protocol scheme (e.g. http or https)
	 * @param host Host name or IP of the server
	 * @param port the port
	 * @param uri the URI to get (default if null or empty = "/")
	 * @param user user name credential (optional)
	 * @param password password for given user name
	 * 
	 * @return The body of the response
	 */
	public String httpGet(String scheme, String host, int port, String uri,
			String user, String password) {
		String URI = "/";

		// Make sure we have a URI
		if (uri != null || uri.trim().length() > 0) {
			URI = uri;
		}

		HttpGet httpget = new HttpGet(URI);

		HttpHost target = new HttpHost(host, port, scheme);

		// retrieve proxy related properties
		final String proxyhost = System.getProperty(PROXY_HOST);
		final String proxyport = System.getProperty(PROXY_PORT);
		final String proxyuser = System.getProperty(PROXY_USER);
		final String proxypass = System.getProperty(PROXY_PASS);
		final String proxydomain = System.getProperty(PROXY_DOMAIN);

		// If there are both proxy host and port values, configure the request
		// to use them
		if (StringUtils.isNotBlank(proxyhost)
				&& StringUtils.isNotBlank(proxyport)) {
			HttpHost proxy = new HttpHost(proxyhost,
					Integer.parseInt(proxyport));
			RequestConfig config = RequestConfig.custom().setProxy(proxy)
					.build();
			httpget.setConfig(config);
			debug("Executing request " + httpget.getRequestLine() + " to "
					+ target + " via " + proxy);
		} else {
			debug("Executing request " + httpget.getRequestLine() + " to "
					+ target);
		}

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();

		// Create a context in which we will execute our request
		HttpClientContext localContext = HttpClientContext.create();

		// lets try creating an AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(target, basicAuth);
		localContext.setAuthCache(authCache);

		// If we have credentials, set them in the local client context
		if (user != null && password != null) {
			CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

			// place the credentials provider in the client context
			localContext.setCredentialsProvider(credentialsProvider);

			// If we have proxy credentials, add them
			if (StringUtils.isNotBlank(proxyhost)
					&& StringUtils.isNotBlank(proxyport)
					&& StringUtils.isNotBlank(proxyuser)
					&& StringUtils.isNotBlank(proxypass)
					&& StringUtils.isNotBlank(proxydomain)) {
				debug("Setting NTLM proxy config");
				credentialsProvider.setCredentials(new AuthScope(proxyhost,
						Integer.parseInt(proxyport), null, "NTLM"),
						new NTCredentials(proxyuser, proxypass, null,
								proxydomain));
			}

			// Now set the credentials for the target host:port
			// credentialsProvider.setCredentials(new
			// AuthScope(target.getHostName(), target.getPort()), new
			// UsernamePasswordCredentials(user, password));
			credentialsProvider.setCredentials(AuthScope.ANY,
					new UsernamePasswordCredentials(user, password));
			// credentialsProvider.setCredentials(new
			// AuthScope(target.getHostName(),
			// target.getPort(),"Service-now",AuthScope.ANY_SCHEME), new
			// UsernamePasswordCredentials(user, password));
			// credentialsProvider.setCredentials( new
			// AuthScope(AuthScope.ANY_HOST,AuthScope.ANY_PORT,"Service-now",AuthScope.ANY_SCHEME),
			// new UsernamePasswordCredentials(user, password));

			// Try a brute-force Basic Auth as describe in HTTP 1.1 section 11.1
			// httpget.addHeader("Authorization", new String("Basic " +
			// Base64.encodeBase64String(new String(user + ":" +
			// password).getBytes())));
			// System.out.println("Auth header:");for(Header header :
			// httpget.getHeaders("Authorization")){ System.out.println(header);
			// }

			// What else and we try?
		}

		try {

			// Create the request for the URI
			HttpGet request = new HttpGet(URI);

			// Set the content type to JSON
			request.addHeader("content-type", "application/json");

			// Execute the request
			CloseableHttpResponse response = httpClient.execute(target,
					httpget, localContext);

			// perform a little debug logging if required
			if (isDebug()) {
				debug("RESPONSE: "
						+ response.getStatusLine().getProtocolVersion() + "  "
						+ response.getStatusLine().getStatusCode() + " - "
						+ response.getStatusLine().getReasonPhrase());
				for (HeaderIterator it = response.headerIterator(); it
						.hasNext();) {
					Header header = (Header) it.next();
					debug("HEADER: " + header);
				}
			}

			String body = EntityUtils.toString(response.getEntity(), "UTF-8");
			debug("BODY: " + body);

			return body;
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * This performs a basic HTTP POST to the given URI with the given body
	 * 
	 * @param host
	 *            Host name or IP of the server
	 * @param port
	 *            the port
	 * @param uri
	 *            the URI to get (default if null or empty = "/")
	 * @param user
	 *            user name credential (optional)
	 * @param password
	 *            password for given user name
	 * @param body
	 *            the body of the post request containing the data to post.
	 * 
	 * @return The HTTP Response
	 */
	public HttpResponse httpPost(String host, int port, String uri,
			String user, String password, String body) {
		String URI = "/";

		// Make sure we have a URI
		if (uri != null || uri.trim().length() > 0) {
			URI = uri;
		}

		HttpHost targetHost = new HttpHost(host, port, "http");
		debug(targetHost + URI);

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		try {

			// Create the request for the URI
			HttpPost request = new HttpPost(URI);

			// set the body of the request
			StringEntity params = new StringEntity(body);
			request.setEntity(params);

			// Set the content type to JSON
			request.addHeader("content-type", "application/json");

			// Create a context in which we will execute our request
			HttpClientContext localContext = HttpClientContext.create();

			// If we have credentials, set them in the local client context
			if (user != null && password != null) {
				CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
				credentialsProvider.setCredentials(AuthScope.ANY,
						new UsernamePasswordCredentials(user, password));
				localContext.setCredentialsProvider(credentialsProvider);
			}

			// Execute the request
			HttpResponse result = httpClient.execute(targetHost, request,
					localContext);

			// perform a little debug logging if required
			if (isDebug()) {
				debug(result.getStatusLine().getProtocolVersion() + "  "
						+ result.getStatusLine().getStatusCode() + " - "
						+ result.getStatusLine().getReasonPhrase());
				for (Iterator it = result.headerIterator(); it.hasNext();) {
					Header header = (Header) it.next();
					debug(header);
				}
				String json = EntityUtils.toString(result.getEntity(), "UTF-8");
				debug(json);
			}

			return result;
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

}
