# Football Data API

A Java-based service to fetch and store football league data from the football-data.org API.

## Stack
- **Java 11**
- **Maven**
- **MySQL 8.0**
- **ActiveJDBC** (ORM)
- **SparkJava** (Web Framework)
- **Docker** & **Docker Compose**

---

## Features
- Import competition, team, and player data by league code.
- Query total players in a league from the local database.

## API Endpoints
- `GET /import-league/{leagueCode}`: Fetches and persists data from external API.
- `GET /total-players/{leagueCode}`: Returns total players count from DB.

## Setup

1. `cp .env.example .env`
2. `docker-compose up --build`
