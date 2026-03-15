package com.football.api.service;

import java.sql.SQLException;

import javax.inject.Singleton;

import com.football.api.exception.NotFoundException;
import com.football.api.model.*;

@Singleton
public class PlayerServiceImpl implements IPlayerService {

    @Override
	public int countPlayersByLeague(String league) throws NotFoundException, SQLException {
        
        if(!checkInDatabase(league)){
            throw new NotFoundException(404, "Not found", null);
        }

        return totalplayers(league);

	}

    private boolean checkInDatabase(String league) {
        return Competition.findFirst("code = ?", league) != null;
    }

    private int totalplayers(String league) throws SQLException{
        Competition c = Competition.findFirst("code = ?", league);
        if (c == null) {
            return 0;
        }
        int idLeague = c.getInteger("id");
        Long count = TeamsPlayers.count("team_id IN (SELECT team_id FROM competitions_teams WHERE competition_id = ?)", idLeague);
        return count.intValue();
    }
}
