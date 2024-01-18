package com.singularity.base.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.singularity.base.dto.BaseResDto;

public class JsonUtils {
    public static final ObjectMapper OM = new ObjectMapper();

    public static <T> T parse(String jsonString, TypeReference<T> tr) {
        try {
            return OM.readValue(jsonString, tr);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public static <T> T parse(String jsonString, Class<T> clazz) {
        try {
            return OM.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public static String stringify(Object obj) {
        try {
            return OM.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertType(Object obj, Class<T> dtoClass) {
        return JsonUtils.parse(JsonUtils.stringify(obj), dtoClass);
    }
}
