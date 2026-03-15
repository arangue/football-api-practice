package com.football.api;

import static spark.Spark.*;

import org.javalite.activejdbc.*;
import com.football.api.controller.ImportController;
import com.football.api.controller.PlayerController;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class App {
    
    public static void main( String[] args ){

      Injector injector = Guice.createInjector(new FootballModule());

      port(4567);

      enableCORS("*","*","*");

      before((request, response) -> {
          if (!Base.hasConnection()) {
              String dbUrl = System.getenv("DB_URL");
              String dbUser = System.getenv("DB_USER");
              String dbPass = System.getenv("DB_PASSWORD");

              if (dbUrl != null && dbUser != null && dbPass != null) {
                  Base.open("com.mysql.cj.jdbc.Driver", dbUrl, dbUser, dbPass);
              } else {
                  Base.open();
              }
              System.out.println("DB connection opened");
          }
      });

      after((request, response) -> {
          if (Base.hasConnection()) {
              Base.close();
              System.out.println("DB connection closed");
          }
          response.type("application/json");
      });

      ImportController importController = injector.getInstance(ImportController.class);
      PlayerController playerController = injector.getInstance(PlayerController.class);

      post("/import-league/:leagueCode", importController::importLeague);

      get("/total-players/:leagueCode", playerController::totalPlayers);

    }

    private static void enableCORS(final String origin, final String methods, final String headers) {

      options("/*", (request, response) -> {
          String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
          if (accessControlRequestHeaders != null) {
              response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
          }

          String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
          if (accessControlRequestMethod != null) {
              response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
          }

          return "OK";
      });

      before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
      });
    }

}
