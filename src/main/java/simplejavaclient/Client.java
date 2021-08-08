package simplejavaclient;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Client {

	private final static String BASE_URL = "https://41.207.169.123:8443/mobilemoneyapi/api/v1";
	private final static String USERNAME = "";
	private final static String PASSWORD = "";

	private static HttpClient getHttpClient() throws Exception {

		RequestConfig.Builder requestBuilder = RequestConfig.custom();

		HttpClientBuilder builder = HttpClientBuilder.create();

		builder.setDefaultRequestConfig(requestBuilder.build());

		builder.setSSLSocketFactory(SSLUtil.getInsecureSSLConnectionSocketFactory());

		HttpClient httpClient = builder.build();

		return httpClient;

	}

	public static void getToken() throws Exception {

		System.out.println("*** Test Http POST request ***");

		String encoding = Base64.getEncoder().encodeToString((USERNAME + ":" + PASSWORD).getBytes());
		String url = BASE_URL + "/getToken";
		HttpPost request = new HttpPost(url);
		
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		node.put("operationType", "1");
		node.put("origin", "sydonia");
		String JSON_STRING = node.toString();

		StringEntity stringEntity = new StringEntity(JSON_STRING);

		request.setEntity(stringEntity);

		// add request headers
		request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		request.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);

		HttpClient client = getHttpClient();

		HttpResponse response = client.execute(request);

		System.out.println("POST request to URL: " + url);

		System.out.println("Response Status    : " + response.getStatusLine().toString());

		System.out.println(EntityUtils.toString(response.getEntity()));

	}

	private static class SSLUtil {

		protected static SSLConnectionSocketFactory getInsecureSSLConnectionSocketFactory()

				throws KeyManagementException, NoSuchAlgorithmException {

			final TrustManager[] trustAllCerts = new TrustManager[] {
					new X509TrustManager() {
						public java.security.cert.X509Certificate[] getAcceptedIssuers() {
							return null;
						}
						public void checkClientTrusted(
								final java.security.cert.X509Certificate[] arg0, final String arg1)
								throws CertificateException {
							// do nothing and blindly accept the certificate

						}
						public void checkServerTrusted(
								final java.security.cert.X509Certificate[] arg0, final String arg1)
								throws CertificateException {
							// do nothing and blindly accept the server

						}
					}

			};

			final SSLContext sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, trustAllCerts, new java.security.SecureRandom());
			final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslcontext, new String[] { "TLSv1" }, null,
					NoopHostnameVerifier.INSTANCE);
			return sslsf;

		}
	}
}
