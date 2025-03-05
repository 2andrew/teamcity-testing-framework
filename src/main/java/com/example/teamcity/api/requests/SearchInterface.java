package com.example.teamcity.api.requests;


public interface SearchInterface {
    Object findAll(String attribute);

    Object search(String searchParameter);
}
