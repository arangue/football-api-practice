package com.football.api.service;

import com.football.api.exception.NotFoundException;
import com.google.gson.JsonObject;

import java.io.IOException;

public interface IAPIClientService {
    JsonObject getLeagueFromApi(String league) throws IOException, NotFoundException;
    JsonObject getTeamsFromApi(String league) throws IOException, NotFoundException;
}
