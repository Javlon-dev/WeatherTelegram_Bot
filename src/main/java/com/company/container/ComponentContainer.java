package com.company.container;


import com.company.application.WeatherTelegramBot;
import com.company.controller.MainController;
import com.company.controller.WeatherController;
import com.company.enums.UnitsStatus;
import com.company.enums.UserStatus;

import java.util.HashMap;
import java.util.Map;


public abstract class ComponentContainer {

    public static WeatherTelegramBot WEATHER_TELEGRAM_BOT;
    public static WeatherController WEATHER_CONTROLLER = new WeatherController();
    public static MainController MAIN_CONTROLLER = new MainController();
    public static final String ADMIN_ID = "490541840";
    public static Map<Long, UserStatus> userStatusMap = new HashMap<>();
    public static Map<Long, UnitsStatus> userUnitsMap = new HashMap<>();


}
