package com.football.api.service;

import com.football.api.exception.NotFoundException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class APIClientService {

    private static final String HOST = "https://api.football-data.org";
    private static final String API_KEY = System.getenv("API_KEY");
    private static final OkHttpClient client = new OkHttpClient();
    private static final Logger logger = LoggerFactory.getLogger(APIClientService.class);

    public static JsonObject getLeagueFromApi(String league) throws IOException, NotFoundException {
        return makeApiCall("/v4/competitions/" + league);
    }

    public static JsonObject getTeamsFromApi(String league) throws IOException, NotFoundException {
        return makeApiCall("/v4/competitions/" + league + "/teams");
    }

    private static JsonObject makeApiCall(String path) throws IOException, NotFoundException {
        if (API_KEY == null || API_KEY.isEmpty()) {
            logger.error("API_KEY environment variable is not set.");
            throw new IllegalStateException("API_KEY environment variable is not set");
        }

        String url = HOST + path;
        Request request = new Request.Builder()
                .url(url)
                .header("X-Auth-Token", API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("API call to {} failed with code {}: {}", url, response.code(), response.message());
                if (response.code() == 404) {
                    throw new NotFoundException(404, "Not found at " + url, null);
                }
                throw new IOException("Unexpected API response code: " + response.code());
            }

            logger.info("Successfully received data from API endpoint: {}", url);
            String body = response.body().string();
            return new Gson().fromJson(body, JsonObject.class);
        } catch (IOException e) {
            logger.error("Error during API call to {}", url, e);
            throw e;
        }
    }
}
