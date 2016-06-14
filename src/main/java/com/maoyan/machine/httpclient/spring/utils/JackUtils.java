package com.maoyan.machine.httpclient.spring.utils;

import java.io.IOException;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Created by lubing on 15/5/13.
 */
public class JackUtils {
    private static ObjectMapper mapper;

    public static synchronized ObjectMapper getMapperInstance(boolean createNew) {
        if (createNew) {
            return new ObjectMapper();
        } else if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.setLocale(Locale.CHINA);
        }
        return mapper;
    }

    public static String toJson(Object param) throws JsonProcessingException {
        ObjectMapper objectMapper = JackUtils.getMapperInstance(false);
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        String dataJson = objectMapper.writeValueAsString(param);
        return dataJson;
    }

    public static String toJsonWithRoot(Object param) throws JsonProcessingException {
        ObjectMapper objectMapper = JackUtils.getMapperInstance(false);
        objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        String dataJson = objectMapper.writeValueAsString(param);
        return dataJson;
    }

    public static <T> T jsonToBeanWithRoot(String json, Class<T> cls) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = JackUtils.getMapperInstance(false);
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        return objectMapper.readValue(json, cls);
    }

    public static <T> T jsonToBean(String json, Class<T> cls) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = JackUtils.getMapperInstance(false);
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
        return objectMapper.readValue(json, cls);
    }

    public static <T> T jsonToBeanByTypeReference(String json, TypeReference<T> typeReference) throws JsonParseException, JsonMappingException,
            IOException {
        ObjectMapper objectMapper = JackUtils.getMapperInstance(false);
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);
        return objectMapper.readValue(json, typeReference);
    }

    public static <T> T jsonToBeanByTypeReferenceWithRoot(String json, TypeReference<T> typeReference) throws JsonParseException,
            JsonMappingException, IOException {
        ObjectMapper objectMapper = JackUtils.getMapperInstance(false);
        objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        return objectMapper.readValue(json, typeReference);
    }
}
