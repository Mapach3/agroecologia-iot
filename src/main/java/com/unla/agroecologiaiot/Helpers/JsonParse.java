package com.unla.agroecologiaiot.Helpers;

import com.google.gson.Gson;

public class JsonParse {

    private static Gson gson = new Gson();

    public static class JsonParser {

        public static String ToJson(Object obj) {
            return gson.toJson(obj);
        }

        public static Object FromJson(String json, Class<Object> typeOf){
            return gson.fromJson(json, typeOf);
        }
    }

}
