/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.sip.ingestion;

import org.apache.http.client.methods.CloseableHttpResponse;

public interface ResponseExtractor<T> {

	T extract(final CloseableHttpResponse response);
}
