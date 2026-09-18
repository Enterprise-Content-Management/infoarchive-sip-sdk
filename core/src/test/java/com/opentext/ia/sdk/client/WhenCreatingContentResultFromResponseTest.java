/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.client.api.ContentResult;
import com.opentext.ia.sdk.client.impl.ContentResultFactory;
import com.opentext.ia.sdk.support.http.MediaTypes;
import com.opentext.ia.sdk.support.http.Response;

public class WhenCreatingContentResultFromResponseTest {

    @Test
    void shouldCreateContentResultAndKeepStreamReadableUntilResultIsClosed() throws IOException {
        ContentResultFactory factory = new ContentResultFactory();
        Response response = mock(Response.class);
        Runnable closeResponse = mock(Runnable.class);

        byte[] data = "hello".getBytes(StandardCharsets.UTF_8);
        InputStream body = new ByteArrayInputStream(data);

        when(response.getBody()).thenReturn(body);
        when(response.getHeaderValue("Content-Length", -1)).thenReturn(data.length);
        when(response.getHeaderValue("Content-Type", MediaTypes.BINARY)).thenReturn(MediaTypes.BINARY);
        when(response.getHeaderValue("Content-Disposition", "")).thenReturn("attachment; filename=test.txt");

        ContentResult result = factory.create(response, closeResponse);

        assertNotNull(result);
        assertEquals("test.txt", result.getName());
        assertEquals(data.length, result.getLength());
        assertEquals("hello", IOUtils.toString(result.getInputStream(), StandardCharsets.UTF_8));

        verify(closeResponse, never()).run();

        result.close();

        verify(closeResponse).run();
    }
}
