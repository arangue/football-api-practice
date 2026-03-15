package com.football.api.service;

import com.football.api.exception.NotFoundException;
import com.google.gson.JsonObject;

// import spark.*;

import java.io.IOException;
import java.sql.SQLException;

public class ImportService {

    public static int importService(String league) {

        if (LeagueService.checkInDatabase(league))
            return 409;
        try {
            JsonObject competitions = APIClientService.getLeagueFromApi(league);
            
            LeagueService.insertCompetition(competitions);

        } catch (IOException | InterruptedException e) {
            return 504; // Gateway Timeout for network/concurrency issues
        } catch (SQLException e) {
            return 500; // Internal Server Error for DB issues
        } catch (NotFoundException e) {
            return 404;
        } 
		return 201; 
    }

}
