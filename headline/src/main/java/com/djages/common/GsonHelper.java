package com.djages.common;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public class GsonHelper {
    private static Gson gson;
    private static JsonParser jsonParser;


    private static class SpecificClassExclusionStrategy implements
            ExclusionStrategy {
        private final Class<?> excludedThisClass;

        public SpecificClassExclusionStrategy(Class<?> excludedThisClass) {
            this.excludedThisClass = excludedThisClass;
        }

        public boolean shouldSkipClass(Class<?> clazz) {
            return excludedThisClass.equals(clazz);
        }

        public boolean shouldSkipField(FieldAttributes f) {
            return excludedThisClass.equals(f.getDeclaredClass());
        }
    }

    public static Gson getGson() {
        if (gson == null) {
//            ExclusionStrategy excludeStrings = new SpecificClassExclusionStrategy(
//                    RbEventMap.class);
            GsonBuilder gsonBuilder = new GsonBuilder();
//            gson = gsonBuilder.setExclusionStrategies(excludeStrings).create();
            gson = gsonBuilder.create();
        }
        return gson;
    }

    public static JsonParser getJsonParser() {
        if (jsonParser == null) {
            jsonParser = new JsonParser();
        }
        return jsonParser;
    }
}