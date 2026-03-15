package com.football.api.service;

import org.javalite.activejdbc.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.football.api.model.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class LeagueService {

    public static boolean checkInDatabase(String league) {
        return Competition.findFirst("code = ?", league) != null;
    }

    public static boolean teamExistOnDB(int team) {
        return Team.findById(team) != null;
    }

    public static boolean playerExistOnDB(int player) {
        return Player.findById(player) != null;
    }
    
    public static void insertCompetition(JsonObject competitions) throws SQLException {
        int id = competitions.get("id").getAsInt();
        String leagueName = competitions.get("name").getAsString();
        String leagueCode = competitions.get("code").getAsString();
        String areaName = competitions.getAsJsonObject("area").get("name").getAsString();

        try {
            Competition c = new Competition();
            c.setId(id);
            c.set("name", leagueName);
            c.set("code", leagueCode);
            c.set("areaName", areaName);
            c.insert();
            
            JsonObject teams = APIClientService.getTeamsFromApi(leagueCode);
            insertTeam(teams, id);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new SQLException();
        }
    }

    public static void insertTeam(JsonObject t, int idComp) throws SQLException {
        JsonArray teams = t.get("teams").getAsJsonArray();

        try {
            for (JsonElement e : teams) {
                int teamId = e.getAsJsonObject().get("id").getAsInt();
                
                if (!teamExistOnDB(teamId)) {
                    String id = e.getAsJsonObject().get("id").getAsString();
                    String name = e.getAsJsonObject().get("name").getAsString();
                    String tla = e.getAsJsonObject().has("tla") && !e.getAsJsonObject().get("tla").isJsonNull() ? e.getAsJsonObject().get("tla").getAsString() : null;
                    String shortName = e.getAsJsonObject().has("shortName") && !e.getAsJsonObject().get("shortName").isJsonNull() ? e.getAsJsonObject().get("shortName").getAsString() : null;
                    JsonElement email = e.getAsJsonObject().get("email");
                    boolean isEmailNull = email == null || email.isJsonNull();
                    String areaName = e.getAsJsonObject().get("area").getAsJsonObject().get("name").getAsString();

                    Competition competition = Competition.findById(idComp);
                    Team team = new Team();
                    team.setId(id);
                    team.set("name", name);
                    if(tla != null) team.set("tla", tla);
                    if(shortName != null) team.set("shortName", shortName);
                    if(!isEmailNull) team.set("email", email.getAsString());
                    team.set("areaName", areaName);
                    team.insert();
                    competition.add(team);
                } else {
                    Competition competition = Competition.findById(idComp);
                    Team team = Team.findById(teamId);
                    competition.add(team);
                }
                
                JsonArray squad = e.getAsJsonObject().has("squad") && !e.getAsJsonObject().get("squad").isJsonNull() ? e.getAsJsonObject().getAsJsonArray("squad") : new JsonArray();
                insertPlayer(teamId, squad);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException();
        }
    }

    public static void insertPlayer(int idTeam, JsonArray players) throws SQLException {
        try {
            for (JsonElement t : players) {
                int idPlayer = t.getAsJsonObject().get("id").getAsInt();
                
                if(!playerExistOnDB(idPlayer)){
                    String name = t.getAsJsonObject().has("name") && !t.getAsJsonObject().get("name").isJsonNull() ? t.getAsJsonObject().get("name").getAsString() : null;
                    String position = getNullAsEmptyString(t.getAsJsonObject().get("position"));
                    String dobString = getNullAsEmptyString(t.getAsJsonObject().get("dateOfBirth"));
                    String countryOfBirth = t.getAsJsonObject().has("countryOfBirth") && !t.getAsJsonObject().get("countryOfBirth").isJsonNull() ? t.getAsJsonObject().get("countryOfBirth").getAsString() : null;
                    String nationality = t.getAsJsonObject().has("nationality") && !t.getAsJsonObject().get("nationality").isJsonNull() ? t.getAsJsonObject().get("nationality").getAsString() : null;

                    Team team = Team.findById(idTeam);
                    Player player = new Player();
                    player.setId(idPlayer);
                    if(name != null) player.set("name",name);
                    if(position != null) player.set("position",position);
                    if(dobString != null) player.set("dateOfBirth",convertDate(dobString));
                    if(countryOfBirth != null) player.set("countryOfBirth",countryOfBirth);
                    if(nationality != null) player.set("nationality",nationality);
                    player.insert();
                    team.add(player);

                } else {
                    Team team = Team.findById(idTeam);
                    Player player = Player.findById(idPlayer);
                    team.add(player);
                }
            }

        } catch (Exception e1) {
            e1.printStackTrace();
            throw new SQLException();
        }        
    }
    
    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return (jsonElement == null || jsonElement.isJsonNull()) ? null : jsonElement.getAsString();
    }

    private static LocalDate convertDate (String dateString) {
        if (dateString.contains("T")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
            return LocalDate.parse(dateString, formatter);
        } else {
            return LocalDate.parse(dateString);
        }
    }
    
}
