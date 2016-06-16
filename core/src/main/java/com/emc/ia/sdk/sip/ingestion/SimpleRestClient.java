/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.sip.ingestion;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleRestClient implements Closeable {

	private final CloseableHttpClient client;
	private final ObjectMapper mapper;
	private final int STATUS_CODE_RANGE_MIN = 200;
	private final int STATUS_CODE_RANGE_MAX = 300;

	public SimpleRestClient() {
		final HttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
		client = HttpClients.custom().setConnectionManager(manager).build();

		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
	}

	public <T> T get(String uri, List<Header> headers, final Class<T> type) {
		try {
			HttpGet getRequest = new HttpGet(uri);
			if (headers != null) {
				for (Header header : headers) {
					getRequest.addHeader(header);
				}
			}

                        ResponseHandler<T> responseHandler = response -> {
                          
                            int status = response.getStatusLine().getStatusCode();
                            
                            //TODO - need to add logging - print status line and status code
                            
                            if (status >= STATUS_CODE_RANGE_MIN && status < STATUS_CODE_RANGE_MAX) {
                              try {
                                HttpEntity entity = response.getEntity();
                                if (entity != null) {
                                    String body = EntityUtils.toString(entity);
                                    return mapper.readValue(body, type);
                			} else {
                				return null;
                			  }
                              } catch (Exception e) {              				
                                      throw new RuntimeException(e);
                		}
                            } else {
                            	throw new ClientProtocolException("Unexpected response status: " + status);
                              }
                        };

			T result = client.execute(getRequest, responseHandler);
			return result;

		} catch (Exception e) {
			throw new RuntimeException(e);
		  }
	}

	@Override
	public void close() {
		try {
			client.close();
		} catch (IOException e) {

		}
	}

	public <T> T get(URI uri, List<Header> headers,	ResponseExtractor<T> extractor) {
		try {
			HttpGet getRequest = new HttpGet(uri);
			if (headers != null) {
				for (Header header : headers) {
					getRequest.addHeader(header);
				}
			}

			CloseableHttpResponse httpResponse = client.execute(getRequest);
			int status = httpResponse.getStatusLine().getStatusCode();

			if (status >= STATUS_CODE_RANGE_MIN && status < STATUS_CODE_RANGE_MAX) {
				T result = extractor.extract(httpResponse);
				return result;
			} else {
				throw new ClientProtocolException(
						"Unexpected response status: " + status);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T post(String uri, List<Header> headers, String body,
			InputStream attachment, Class<T> type) {
		try {
			HttpPost postRequest = new HttpPost(uri);
			if (headers != null) {
				for (Header header : headers) {
					postRequest.addHeader(header);
				}
			}
			//TODO - what should be the file name here ? IASIP.zip is Ok ?
			InputStreamBody file = new InputStreamBody(attachment,
					ContentType.APPLICATION_OCTET_STREAM, "IASIP.zip") {

			};
			
			HttpEntity entity = MultipartEntityBuilder.create()
					.addTextBody("format", "sip_zip").addPart("sip", file)
					.build();

			postRequest.setEntity(entity);

			T result = client.execute(postRequest, response -> {
			  int status = response.getStatusLine().getStatusCode();			  
			  
			  if (status >= STATUS_CODE_RANGE_MIN && status < STATUS_CODE_RANGE_MAX) {
			    try {
			      HttpEntity entity1 = response.getEntity();
			      if (entity1 != null) {
			        String body1 = EntityUtils.toString(entity1);			        
			        return mapper.readValue(body1, type);			        
				} else {
				    return null;
				  }
			    } catch (Exception e) {
				throw new RuntimeException(e);
				}

			    } else {      
      			        throw new ClientProtocolException("Unexpected response status: " + status);
  			    }
			 });

			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T put(String uri, List<Header> headers, Class<T> type) {

		try {
			HttpPut putRequest = new HttpPut(uri);
			if (headers != null) {
				for (Header header : headers) {
					putRequest.addHeader(header);
				}
			}
			
			T result = client.execute(putRequest, response -> {
			  int status = response.getStatusLine().getStatusCode();			 
			  
			  if (status >= STATUS_CODE_RANGE_MIN && status < STATUS_CODE_RANGE_MAX) {
			    try {
			      HttpEntity entity1 = response.getEntity();
			      if (entity1 != null) {
			        String body1 = EntityUtils.toString(entity1);
			        System.err.println(body1);
			        return mapper.readValue(body1, type);
				} else {
				  return null;
				}
			     } catch (Exception e1) {
				// TODO:
				throw new RuntimeException(e1);
			      }

			  } else {
			    throw new ClientProtocolException("Unexpected response status: " + status);
			    }
			});

			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
