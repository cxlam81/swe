package com.nphc.rest;

import com.google.gson.Gson;
import com.nphc.data.User;
import lombok.Data;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

@ControllerAdvice(basePackages = "com.nphc.rest")
public class JSONResponseWrapper implements ResponseBodyAdvice {
    //enable Gson
    private static final Gson gson = new Gson();

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (body instanceof List){
            return new Wrapper<>((List<Object>) body);
        }
        return gson.toJson( new MessageWrapper((String) body) );
    }

    @Data
    private class MessageWrapper{
        private String message;

        public MessageWrapper(String restResponse) {
            this.message = restResponse;
        }
    }

    @Data
    private class Wrapper<T>{
        private final List<T> results;

        public Wrapper(List<T> list) {
            this.results = list;
        }
    }
}
