package com.football.api.service;

import com.football.api.exception.DatabaseException;
import com.football.api.exception.LeagueAlreadyExistsException;
import com.football.api.exception.NetworkException;
import com.football.api.exception.NotFoundException;

public interface IImportService {
    void importLeague(String leagueCode)
            throws LeagueAlreadyExistsException, NotFoundException, NetworkException, DatabaseException;
}
