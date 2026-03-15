package com.football.api.service;

import com.football.api.exception.NotFoundException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class APIClientService {
    
    private static final String HOST = "https://api.football-data.org";
    private static final String API_KEY = System.getenv("API_KEY");
    private static final OkHttpClient client = new OkHttpClient();
    
    public static JsonObject getLeagueFromApi(String league) throws IOException, InterruptedException, NotFoundException {
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new IllegalStateException("API_KEY environment variable is not set");
        }
        String url = HOST+"/v4/competitions/"+league;
        Request request = new Request.Builder()
        .url(url)
        .header("X-Auth-Token", API_KEY)
        .build();
        Response response = client.newCall(request).execute();
        
        if (response.code() == 404)
            throw new NotFoundException(404, "Not found", null);
        
        String body = response.body().string();
        response.body().close();
        JsonObject convertedObject = new Gson().fromJson(body, JsonObject.class);
        return convertedObject; 
    }
    
    public static JsonObject getTeamsFromApi(String league) throws IOException, InterruptedException, NotFoundException {
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new IllegalStateException("API_KEY environment variable is not set");
        }
        String url = HOST+"/v4/competitions/"+league+"/teams";
        Request request = new Request.Builder()
        .url(url)
        .header("X-Auth-Token", API_KEY)
        .build();
        Response response = client.newCall(request).execute();
        
        if (response.code() == 404)
            throw new NotFoundException(404, "Not found", null);
        
        String body = response.body().string();
        response.body().close();
        JsonObject convertedObject = new Gson().fromJson(body, JsonObject.class);
        return convertedObject;

    }

}
