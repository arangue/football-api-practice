package com.football.api.exception;

public class LeagueAlreadyExistsException extends Exception {
    public LeagueAlreadyExistsException(String message) {
        super(message);
    }
}
