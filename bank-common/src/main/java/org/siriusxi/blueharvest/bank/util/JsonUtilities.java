package org.siriusxi.blueharvest.bank.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class JsonUtilities {

    private JsonUtilities(){}

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object object) throws IOException {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsString(object);
    }

    public static byte[] toJsonAsByte(Object object) throws IOException {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
}
