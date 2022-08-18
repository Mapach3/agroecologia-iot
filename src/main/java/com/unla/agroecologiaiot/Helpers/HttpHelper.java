package com.unla.agroecologiaiot.Helpers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class HttpHelper {

    private static HttpHeaders headers = new HttpHeaders();

    public static class Http {

        public static HttpHeaders GetContentType_Json() {
            headers.setContentType(MediaType.APPLICATION_JSON);
            return headers;
        }

    }

}