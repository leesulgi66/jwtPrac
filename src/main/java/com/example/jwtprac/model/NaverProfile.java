package com.example.jwtprac.model;

import lombok.Data;

import javax.annotation.Generated;

@Data
@Generated("jsonschema2pojo")
public class NaverProfile {

    public String resultcode;
    public String message;
    public Response response;
    public class Response {
        public String id;
        public String profile_image;
        public String email;
        public String name;
    }
}



