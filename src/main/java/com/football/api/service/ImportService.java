package com.football.api.service;

import com.football.api.exception.DatabaseException;
import com.football.api.exception.LeagueAlreadyExistsException;
import com.football.api.exception.NetworkException;
import com.football.api.exception.NotFoundException;
import com.google.gson.JsonObject;
import org.javalite.activejdbc.Base;

import java.io.IOException;

public class ImportService {

    public static void importLeague(String leagueCode)
            throws LeagueAlreadyExistsException, NotFoundException, NetworkException, DatabaseException {

        if (LeagueService.checkInDatabase(leagueCode)) {
            throw new LeagueAlreadyExistsException("League with code '" + leagueCode + "' already exists.");
        }

        try {
            Base.openTransaction();

            JsonObject competitions = APIClientService.getLeagueFromApi(leagueCode);
            LeagueService.insertCompetition(competitions);

            Base.commitTransaction();
        } catch (IOException e) {
            Base.rollbackTransaction();
            throw new NetworkException("Failed to fetch data from external API.", e);
        } catch (Exception e) {
            Base.rollbackTransaction();
            throw new DatabaseException("Failed to import data into the database.", e);
        }
    }
}
