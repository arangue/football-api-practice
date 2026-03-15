package com.football.api.controller;

import java.sql.SQLException;

import com.football.api.exception.NotFoundException;
import com.football.api.service.IPlayerService;
import com.google.gson.JsonObject;
import spark.*;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PlayerController {

    private final IPlayerService playerService;

    @Inject
    public PlayerController(IPlayerService playerService) {
        this.playerService = playerService;
    }

    public JsonObject totalPlayers(Request request, Response response) {

        String league = request.params(":leagueCode");
        try{ 
            int total = playerService.countPlayersByLeague(league);   
            return getJson(String.valueOf(total), 200, response);
        }catch(NotFoundException e){            
            return getJson("Not Found", 404, response);
        }catch(SQLException e){
            return getJson("Internal Server Error", 500, response);
        }
    }

    private JsonObject getJson(String msg, int status, Response response){
        JsonObject json = new JsonObject();
        json.addProperty("total", msg);
        response.status(status);
        return json; 
    }     
}
