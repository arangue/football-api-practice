package com.football.api.service;

import com.football.api.exception.NotFoundException;
import com.football.api.model.Competition;
import com.football.api.model.Player;
import com.football.api.model.Team;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.javalite.activejdbc.DBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LeagueService {

    private static final Logger logger = LoggerFactory.getLogger(LeagueService.class);

    public static boolean checkInDatabase(String league) {
        logger.debug("Checking if league '{}' exists in database.", league);
        return Competition.findFirst("code = ?", league) != null;
    }

    public static boolean teamExistOnDB(int team) {
        return Team.findById(team) != null;
    }

    public static boolean playerExistOnDB(int player) {
        return Player.findById(player) != null;
    }

    public static void insertCompetition(JsonObject competitions) throws IOException, NotFoundException, DBException {
        int id = competitions.get("id").getAsInt();
        String leagueName = competitions.get("name").getAsString();
        String leagueCode = competitions.get("code").getAsString();
        String areaName = competitions.getAsJsonObject("area").get("name").getAsString();

        logger.info("Inserting competition: {} ({})", leagueName, leagueCode);
        Competition c = new Competition();
        c.setId(id);
        c.set("name", leagueName);
        c.set("code", leagueCode);
        c.set("areaName", areaName);
        c.insert();

        JsonObject teams = APIClientService.getTeamsFromApi(leagueCode);
        insertTeam(teams, id);
    }

    public static void insertTeam(JsonObject t, int idComp) throws IOException, NotFoundException, DBException {
        JsonArray teams = t.get("teams").getAsJsonArray();
        logger.info("Processing {} teams for competition ID: {}", teams.size(), idComp);

        for (JsonElement e : teams) {
            int teamId = e.getAsJsonObject().get("id").getAsInt();
            Competition competition = Competition.findById(idComp);

            if (!teamExistOnDB(teamId)) {
                String id = e.getAsJsonObject().get("id").getAsString();
                String name = e.getAsJsonObject().get("name").getAsString();
                String tla = e.getAsJsonObject().has("tla") && !e.getAsJsonObject().get("tla").isJsonNull() ? e.getAsJsonObject().get("tla").getAsString() : null;
                String shortName = e.getAsJsonObject().has("shortName") && !e.getAsJsonObject().get("shortName").isJsonNull() ? e.getAsJsonObject().get("shortName").getAsString() : null;
                JsonElement email = e.getAsJsonObject().get("email");
                boolean isEmailNull = email == null || email.isJsonNull();
                String areaName = e.getAsJsonObject().get("area").getAsJsonObject().get("name").getAsString();

                logger.debug("Inserting new team: {} ({})", name, teamId);
                Team team = new Team();
                team.setId(id);
                team.set("name", name);
                if (tla != null) team.set("tla", tla);
                if (shortName != null) team.set("shortName", shortName);
                if (!isEmailNull) team.set("email", email.getAsString());
                team.set("areaName", areaName);
                team.insert();
                competition.add(team);
            } else {
                logger.debug("Team {} already exists, associating with competition.", teamId);
                Team team = Team.findById(teamId);
                competition.add(team);
            }

            JsonArray squad = e.getAsJsonObject().has("squad") && !e.getAsJsonObject().get("squad").isJsonNull() ? e.getAsJsonObject().getAsJsonArray("squad") : new JsonArray();
            insertPlayer(teamId, squad);
        }
    }

    public static void insertPlayer(int idTeam, JsonArray players) throws DBException {
        logger.info("Processing {} players for team ID: {}", players.size(), idTeam);
        for (JsonElement t : players) {
            int idPlayer = t.getAsJsonObject().get("id").getAsInt();
            Team team = Team.findById(idTeam);

            if (!playerExistOnDB(idPlayer)) {
                String name = t.getAsJsonObject().has("name") && !t.getAsJsonObject().get("name").isJsonNull() ? t.getAsJsonObject().get("name").getAsString() : null;
                String position = getNullAsEmptyString(t.getAsJsonObject().get("position"));
                String dobString = getNullAsEmptyString(t.getAsJsonObject().get("dateOfBirth"));
                String countryOfBirth = t.getAsJsonObject().has("countryOfBirth") && !t.getAsJsonObject().get("countryOfBirth").isJsonNull() ? t.getAsJsonObject().get("countryOfBirth").getAsString() : null;
                String nationality = t.getAsJsonObject().has("nationality") && !t.getAsJsonObject().get("nationality").isJsonNull() ? t.getAsJsonObject().get("nationality").getAsString() : null;

                logger.debug("Inserting new player: {} ({})", name, idPlayer);
                Player player = new Player();
                player.setId(idPlayer);
                if (name != null) player.set("name", name);
                if (position != null) player.set("position", position);
                if (dobString != null) player.set("dateOfBirth", convertDate(dobString));
                if (countryOfBirth != null) player.set("countryOfBirth", countryOfBirth);
                if (nationality != null) player.set("nationality", nationality);
                player.insert();
                team.add(player);
            } else {
                logger.debug("Player {} already exists, associating with team.", idPlayer);
                Player player = Player.findById(idPlayer);
                team.add(player);
            }
        }
    }

    private static String getNullAsEmptyString(JsonElement jsonElement) {
        return (jsonElement == null || jsonElement.isJsonNull()) ? null : jsonElement.getAsString();
    }

    private static LocalDate convertDate(String dateString) {
        if (dateString == null) return null;
        if (dateString.contains("T")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
            return LocalDate.parse(dateString, formatter);
        } else {
            return LocalDate.parse(dateString);
        }
    }
}
