/*
 * Copyright (c) 2016-2017 by OpenText Corporation. All Rights Reserved.
 */
package com.opentext.ia.sdk.support.http.apache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.opentext.ia.sdk.support.http.Response;
import com.opentext.ia.sdk.support.http.ResponseBodyFactory;
import com.sun.net.httpserver.HttpServer;

class WhenStreamingResponsesUsingApacheTest {

    private static final String BODY = "streamed-payload";

    private HttpServer server;
    private ApacheHttpClient httpClient;
    private String uri;

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/content", exchange -> {
            byte[] bytes = BODY.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/octet-stream");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        uri = "http://127.0.0.1:" + server.getAddress().getPort() + "/content";
        httpClient = new ApacheHttpClient();
    }

    @AfterEach
    void stopServer() {
        httpClient.close();
        server.stop(0);
    }

    @Test
    void shouldKeepResponseStreamReadableAfterExecuteReturns() throws IOException {
        AtomicBoolean closed = new AtomicBoolean();

        // Factory hands the live stream back to the caller, like ContentResultFactory does
        StreamHolder holder = httpClient.get(uri, Collections.emptyList(),
            new ResponseBodyFactory<StreamHolder>() {

                @Override
                protected StreamHolder doCreate(Response response, InputStream resultStream, Runnable closeResult) {
                    return new StreamHolder(resultStream, () -> {
                        closed.set(true);
                        closeResult.run();
                    });
                }
            });

        // execute(...) has returned - with the response-handler overload this stream would already be closed
        assertFalse(closed.get(), "Closed too early");
        assertEquals(BODY, IOUtils.toString(holder.stream, StandardCharsets.UTF_8), "Body");

        holder.close();
        assertTrue(closed.get(), "Not closed");
    }

    private static final class StreamHolder {

        private final InputStream stream;
        private final Runnable closeResponse;

        StreamHolder(InputStream stream, Runnable closeResponse) {
            this.stream = stream;
            this.closeResponse = closeResponse;
        }

        void close() {
            closeResponse.run();
        }
    }

}
