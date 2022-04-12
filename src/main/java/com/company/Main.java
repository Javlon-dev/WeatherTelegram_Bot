package com.company;

import com.company.application.WeatherTelegramBot;
import com.company.container.ComponentContainer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            WeatherTelegramBot weatherTelegramBot = new WeatherTelegramBot();
            ComponentContainer.WEATHER_TELEGRAM_BOT = weatherTelegramBot;
            telegramBotsApi.registerBot(weatherTelegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
