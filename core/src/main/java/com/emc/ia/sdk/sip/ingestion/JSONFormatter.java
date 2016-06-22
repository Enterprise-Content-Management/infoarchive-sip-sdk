/*
 * Copyright (c) 2016 EMC Corporation. All Rights Reserved.
 * EMC Confidential: Restricted Internal Distribution
 */
package com.emc.ia.sdk.sip.ingestion;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JSONFormatter {

    @SuppressWarnings("deprecation")
    public String format(Object value) {
        try {
            if (value == null) {
              throw new JsonGenerationException("String is null");
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false); 
            mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
            return mapper.writer().writeValueAsString(value);
        } catch (JsonProcessingException e) {            
            throw new RuntimeException(e);
        }
    }
    
}
