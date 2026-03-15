package com.football.api.service;

import com.google.gson.JsonObject;
import org.javalite.activejdbc.DBException;
import com.football.api.exception.NotFoundException;

import java.io.IOException;

public interface ILeagueService {
    boolean checkInDatabase(String league);
    boolean teamExistOnDB(int team);
    boolean playerExistOnDB(int player);
    void insertCompetition(JsonObject competitions) throws IOException, NotFoundException, DBException;
    void insertTeam(JsonObject t, int idComp) throws IOException, NotFoundException, DBException;
    void insertPlayer(int idTeam, com.google.gson.JsonArray players) throws DBException;
}
