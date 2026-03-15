package com.football.api.controller;

import com.football.api.exception.DatabaseException;
import com.football.api.exception.LeagueAlreadyExistsException;
import com.football.api.exception.NetworkException;
import com.football.api.exception.NotFoundException;
import com.football.api.service.ImportService;
import com.google.gson.JsonObject;
import spark.Request;
import spark.Response;

public class ImportController {

    public static JsonObject importLeague(Request request, Response response) {
        String leagueCode = request.params(":leagueCode");
        JsonObject responseJson = new JsonObject();

        try {
            ImportService.importLeague(leagueCode);
            response.status(201); // Created
            responseJson.addProperty("message", "Successfully imported league: " + leagueCode);
        } catch (LeagueAlreadyExistsException e) {
            response.status(409); // Conflict
            responseJson.addProperty("message", e.getMessage());
        } catch (NotFoundException e) {
            response.status(404); // Not Found
            responseJson.addProperty("message", "League code not found by the external API.");
        } catch (NetworkException e) {
            response.status(504); // Gateway Timeout
            responseJson.addProperty("message", "A network error occurred while fetching data.");
        } catch (DatabaseException e) {
            response.status(500); // Internal Server Error
            responseJson.addProperty("message", "An error occurred while writing to the database.");
        }

        return responseJson;
    }
}