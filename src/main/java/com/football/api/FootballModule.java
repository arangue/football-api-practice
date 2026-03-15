package com.football.api;

import com.football.api.service.IAPIClientService;
import com.football.api.service.APIClientServiceImpl;
import com.football.api.service.ILeagueService;
import com.football.api.service.LeagueServiceImpl;
import com.football.api.service.IPlayerService;
import com.football.api.service.PlayerServiceImpl;
import com.football.api.service.IImportService;
import com.football.api.service.ImportServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.gson.Gson;
import okhttp3.OkHttpClient;

public class FootballModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IAPIClientService.class).to(APIClientServiceImpl.class).in(Singleton.class);
        bind(ILeagueService.class).to(LeagueServiceImpl.class).in(Singleton.class);
        bind(IPlayerService.class).to(PlayerServiceImpl.class).in(Singleton.class);
        bind(IImportService.class).to(ImportServiceImpl.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient();
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new Gson();
    }
}
