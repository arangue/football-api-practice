package com.football.api.service;

import com.football.api.exception.NotFoundException;
import java.sql.SQLException;

public interface IPlayerService {
    int countPlayersByLeague(String league) throws NotFoundException, SQLException;
}
