/* 
 * Author : Shaik Magdhum Nawaz
 * Email : shaik.nawaz@mastechinfotrellis.com
 * 
 * Mastech InfoTrellis Confidential
 * Copyright InfoTrellis India Pvt. Ltd. 2022
 * The source code for this program is not published. 
 */

package com.mit.gov.ms.common;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.wink.json4j.JSON;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONArtifact;
import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.SSLContext;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class HttpUtils {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpUtils.class);
    
    private static String status = "";

    public static CloseableHttpClient getHttpClient(SSLContext sslContext) {
        HttpClientBuilder b = HttpClientBuilder.create();
        b.setSSLContext(sslContext);
        return b.build();
    }
    
    public static SSLContext getSSLTrustAllCertContext() throws Exception {
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                return true;
            }
        }).setProtocol("TLSv1.2").build();

        return sslContext;
    }
    
    public static CloseableHttpClient getHttpClient(SSLContext sslContext, HostnameVerifier hostnameVerifier) {
        HttpClientBuilder b = HttpClientBuilder.create();
        b.setSSLContext(sslContext);
        b.setSSLHostnameVerifier(hostnameVerifier);
        return b.build();
    }

    public static HostnameVerifier getTrustAllHostName() {
        return new HostnameVerifier() {
            
            @Override
            public boolean verify(String hostname, SSLSession session) {
                LOGGER.info("Allowing SSL HostName - " + hostname);
                return true;
            }
        };
    }
        
    public static HttpResponse executeRestAPI(String url, 
            String method, Map<String, String> header, HttpEntity bodyEntity ) throws InsightsException {
        try {
            return executeRestAPI(HttpUtils.getHttpClient(HttpUtils.getSSLTrustAllCertContext(), HttpUtils.getTrustAllHostName()), url, method, header, bodyEntity);
        } catch (Exception e) {
            LOGGER.error("Error while executeRestAPI - url="+url, e);
            throw new InsightsException("", e);
        }
    }
    
	public static HttpResponse executeRestAPI(CloseableHttpClient httpClient, String url, 
		    String method, Map<String, String> header, HttpEntity bodyEntity ) throws InsightsException {
			HttpRequestBase baseRequest = null;
			//CloseableHttpClient httpClient = null;
			HttpResponse httpResponse = null;
			if (httpClient == null) {
			    httpClient = HttpClients.createDefault();
			}
			
			switch (method.toUpperCase()) {

				case "GET":
					
					HttpGet getRequest = new HttpGet(url);
					addHeader(getRequest, header);
					baseRequest = getRequest;
					httpResponse = executeRequest( baseRequest, httpClient );
					
					break;
	
				case "POST":
					
					HttpPost postRequest = new HttpPost(url);
					addHeader(postRequest, header);
					if (bodyEntity != null) {
					    postRequest.setEntity(bodyEntity);
					}
					baseRequest = postRequest;
					httpResponse = executeRequest( baseRequest, httpClient );
					break;
	
				case "DELETE":
					HttpDelete deleteRequest = new HttpDelete(url);
					addHeader(deleteRequest, header);
					baseRequest = deleteRequest;
					httpResponse = executeRequest( baseRequest, httpClient );
					break;
	
				case "PUT":
					HttpPut putRequest = new HttpPut(url);
					addHeader(putRequest, header);
                    if (bodyEntity != null) {
                        putRequest.setEntity(bodyEntity);
                    }
					baseRequest = putRequest;
					httpResponse = executeRequest( baseRequest, httpClient );
					break;
                case "PATCH":
                case "PATCHJSON":
                    HttpPatch patchRequest = new HttpPatch(url);
                    addHeader(patchRequest, header);
                    if (bodyEntity != null) {
                        patchRequest.setEntity(bodyEntity);
                    }
                    baseRequest = patchRequest;
                    httpResponse = executeRequest( baseRequest, httpClient );
                    break;
				default:
				    LOGGER.error("METHOD not implemented - " + method);
				    return httpResponse;
				    
			}
		return httpResponse;

	}

	public static HttpEntity getHTTPEntity(String body) throws InsightsException {
	    if (body != null) {
	        try {   
	            return new StringEntity(body);
	        }  catch (Exception e) {
	            throw new InsightsException("Error while getHTTPEntity", e);
	        }
	    }
	    return null;
	}
    
	public static HttpEntity getHTTPEntity(List<NameValuePair> body) throws InsightsException {
		if (body!=null && (!body.isEmpty())) {
            try {
                return new UrlEncodedFormEntity(body);
            }catch (UnsupportedEncodingException e) {
                 throw new InsightsException("Error while getHTTPEntity", e);
            }
        }
        return null;
	}

    public static HttpEntity getHTTPEntity(InputStream bodyIn, ContentType contentType) throws InsightsException {
        if (bodyIn != null) {
            try {
                return new InputStreamEntity(bodyIn, contentType);
            }catch (IllegalArgumentException e) {
                 throw new InsightsException("Error while getHTTPEntity", e);
            }
        }
        return null;
    }
	
	public static void addHeader(HttpRequestBase request, Map<String, String> header) {
		if (header != null && !header.isEmpty()) {
		    for (Entry<String, String> kv: header.entrySet()) {
		        request.addHeader(kv.getKey(), kv.getValue());
		    }
		}
	}

	public static HttpResponse executeRequest( HttpRequestBase httprequest,
	    CloseableHttpClient httpClient) throws InsightsException {
        //LOGGER.debug(httprequest.toString());
        //for (Header hdr : httprequest.getAllHeaders()) {
        //    LOGGER.debug(hdr.getName() + "=" + hdr.getValue());
        //}
        HttpResponse httpResponse = null;
        try {
            httpResponse = executeRequestTimed(httprequest, httpClient, 180);
        } catch (Exception e) {
            throw new InsightsException("Error while executeRequest", e);
        }
        return httpResponse;
	}
	
	private static HttpResponse executeRequestTimed( HttpRequestBase httprequest,
	    CloseableHttpClient httpClient, int sec) throws InsightsException {
		HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httprequest);
            //LOGGER.debug("" + httpResponse.getStatusLine().getStatusCode() + "-"
            //        + httpResponse.getStatusLine().getReasonPhrase());
        } catch (ConnectException e1) {
            if (e1.getMessage().contains("Connection timed out") && sec > 0) {
                LOGGER.error("Connection timed out, will try again after 10 sec..");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) { }
                return executeRequestTimed(httprequest, httpClient, sec - 10);
            } else {
                throw new InsightsException("Error while executeRequest " + httprequest.getMethod() + " " + httprequest.getURI(), e1);
            }
        } catch (IOException e) {
            throw new InsightsException("Error while executeRequest", e);
        } 
		return httpResponse;
	}

    public static JSONObject parseResponseAsJSONObject(HttpResponse res) throws InsightsException {
        if (res != null && res.getEntity() != null) {
            try {
                String strres = IOUtils.toString((res.getEntity().getContent()), "UTF-8");
                try {
                    JSONArtifact result = JSON.parse(strres);
                    if (result instanceof JSONObject) {
                        return (JSONObject) result;
                    } else if (result instanceof JSONArray) {
                        return new JSONObject().put("result", (JSONArray) result );
                    }
                    else {
                        return new JSONObject(result);
                    }
                } catch (JSONException e) {
                    return new JSONObject().put("result", strres);
                }
            } catch (IOException | NullPointerException | JSONException e) {
                throw new InsightsException("Error while parsing response", e);
            }
        }
        return null;
    }

    public static String parseResponseAsString(HttpResponse res) throws InsightsException {
        if (res != null && res.getEntity() != null) {
            try {
                return IOUtils.toString(res.getEntity().getContent(), "UTF-8");
            } catch (IOException | NullPointerException e) {
                throw new InsightsException("Error while parsing response", e);
            }
        }
        return null;
    }
    
    public static String parseResponseAsXMLDocument(HttpResponse res, String nName, String attrName)
        throws InsightsException {

        if (res != null && res.getEntity() != null) {
            try {
                String strres = IOUtils.toString((res.getEntity().getContent()), "UTF-8");

                InputStream inputStream = new ByteArrayInputStream(strres.getBytes());

                SAXParserFactory factory = SAXParserFactory.newInstance();

                SAXParser saxParser = factory.newSAXParser();

                // disable external entities
                factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);

                saxParser.parse(inputStream, new DefaultHandler() {

                    @Override
                    public void startElement(String uri, String localName, String qName, Attributes attributes)
                        throws SAXException {
                        if (qName.equalsIgnoreCase(nName)) {
                            status = attributes.getValue(attrName);
                        }
                    }
                });
                return status;
            } catch (SAXException | IOException | NullPointerException | ParserConfigurationException e) {
                throw new InsightsException("Error while parsing response", e);
            }
        }
        return null;
    }

}
