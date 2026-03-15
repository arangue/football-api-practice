package com.football.api.service;

import java.sql.SQLException;
import java.util.List;

import com.football.api.exception.NotFoundException;
import com.football.api.model.*;

public class PlayerService {

	public static int countPlayersByLeague(String league) throws NotFoundException, SQLException {
        
        if(!checkInDatabase(league)){
            throw new NotFoundException(404, "Not found", null);
        }

        return totalplayers(league);

	}

    private static boolean checkInDatabase(String league) {
        return Competition.findFirst("code = ?", league) != null;
    }

    private static int totalplayers(String league) throws SQLException{
        Competition c = Competition.findFirst("code = ?", league);
        if (c == null) {
            return 0;
        }
        int idLeague = c.getInteger("id");
        Long count = TeamsPlayers.count("team_id IN (SELECT team_id FROM competitions_teams WHERE competition_id = ?)", idLeague);
        return count.intValue();
    }
}