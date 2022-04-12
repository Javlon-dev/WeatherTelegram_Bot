package com.company.controller;

import com.company.util.InlineButtonUtil;
import com.company.container.ComponentContainer;
import com.company.enums.UnitsStatus;
import com.company.enums.UserStatus;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class WeatherController {

    private final String token = "45ee1e4a5e8e17e6f21dc1f6a9bad670";

    public void handleText(User user, Message message) {
//        Optional<String> text = Optional.ofNullable(message.getText());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));
        if (ComponentContainer.userStatusMap.containsKey(user.getId())) {
            switch (ComponentContainer.userStatusMap.get(user.getId())) {
                case ShowWeatherDay -> {
                    String sendText = "";
                    JSONObject jsonObject;
                    if (message.hasLocation()) {
                        Location location = message.getLocation();
                        jsonObject = jsonWeatherDay(String.valueOf(location.getLatitude())
                                , String.valueOf(location.getLongitude()), user);
                    } else {
                        jsonObject = jsonWeatherDay(message.getText(), user);
                    }
                    try {
                        sendText = printWeatherDay(jsonObject, user);
                        sendMessage.setText(sendText);
                        ComponentContainer.userStatusMap.put(user.getId(), UserStatus.ShowWeatherDay);
                        sendMessage.setReplyMarkup(InlineButtonUtil.singleKeyboard());
                        ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
                    } catch (RuntimeException e) {
                        e.getMessage();
                    }
                    if (sendText.isEmpty()) {
                        sendMessage.setText("\uD83D\uDE41");
                        ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
                        sendMessage.setText("Not found");
                        ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
                    }

                }
                case ShowWeatherWeek -> {
                    List<String> list = new LinkedList<>();
                    try {
                        if (message.hasLocation()) {
                            Location location = message.getLocation();
                            list = jsonWeatherWeek(String.valueOf(location.getLatitude())
                                    , String.valueOf(location.getLongitude()), user);
                        } else {
                            list = jsonWeatherWeek(message.getText(), user);
                        }
                        sendMessageWeek(list, sendMessage);
                        ComponentContainer.userStatusMap.put(user.getId(), UserStatus.ShowWeatherDay);
                        sendMessage.setText("Tugadi \uD83D\uDE01");
                        sendMessage.setReplyMarkup(InlineButtonUtil.singleKeyboard());
                        ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
                    } catch (RuntimeException e) {
                        e.getMessage();
                    }
                    if (list.isEmpty()) {
                        sendMessage.setText("\uD83D\uDE41");
                        ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
                        sendMessage.setText("Not found");
                        ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
                    }

                }
                case SimpleUser -> ComponentContainer.MAIN_CONTROLLER.handleText(user, message);
            }
        }
    }

    private void sendMessageWeek(List<String> list, SendMessage sendMessage) {
        for (String weather : list) {
            sendMessage.setText(weather);
            ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
        }
    }

    private List<String> jsonWeatherWeek(String cityName, User user) {
        String json = getWeatherJsonWeek(cityName, user);
        JSONObject jsonObject = new JSONObject(json);
        return printWeatherWeek(jsonObject.getJSONArray("list"), jsonObject.getJSONObject("city"), user);
    }

    private List<String> jsonWeatherWeek(String lat, String lon, User user) {
        String json = getWeatherJsonWeek(lat, lon, user);
        JSONObject jsonObject = new JSONObject(json);
        return printWeatherWeek(jsonObject.getJSONArray("list"), jsonObject.getJSONObject("city"), user);
    }

    private List<String> printWeatherWeek(JSONArray jsonArray, JSONObject jsonObject, User user) {
        List<String> list = new LinkedList<>();
        for (int i = 1; i <= 39; i += 7) {
            StringBuilder builder = new StringBuilder();
            builder.append("Country: ");
            builder.append(jsonObject.get("country") + " \uD83C\uDF0F");
            builder.append("\nCity: ");
            builder.append(jsonObject.get("name") + "  \uD83C\uDFD9");
            builder.append("\nTemp: ");
            builder.append(jsonArray.getJSONObject(i).getJSONObject("main").get("temp") + unitsType(user)[0]
                    + "  " + cloudType(jsonArray.getJSONObject(i).getJSONArray("weather")
                    .getJSONObject(0).getString("main")));
            builder.append("\nWind: ");
            builder.append(jsonArray.getJSONObject(i).getJSONObject("wind").get("speed") + unitsType(user)[1]
                    + "  \uD83D\uDCA8");
            builder.append("\nDate: ");
            builder.append(jsonArray.getJSONObject(i).getString("dt_txt").split(" ")[0] + "  \uD83D\uDCC6");
            list.add(builder.toString());
        }
        return list;
    }

    private String cloudType(String cloud) {
        return switch (cloud) {
            case "Rain" -> "\uD83C\uDF27";
            case "Clouds" -> "⛅️";
            case "Snow" -> "\uD83C\uDF28";
            default -> "\uD83C\uDF24";
        };
    }

    private String[] unitsType(User user) {
        String[] units = new String[2];
        if (ComponentContainer.userUnitsMap.get(user.getId()).equals(UnitsStatus.standard)) {
            units = new String[]{" °K", " km/h"};
        } else if (ComponentContainer.userUnitsMap.get(user.getId()).equals(UnitsStatus.metric)) {
            units = new String[]{" °C", " km/h"};
        } else if (ComponentContainer.userUnitsMap.get(user.getId()).equals(UnitsStatus.imperial)) {
            units = new String[]{" °F", " mph"};
        }
        return units;
    }

    private String printWeatherDay(JSONObject jsonObject, User user) {
        StringBuilder builder = new StringBuilder();
        builder.append("Country: ");
        builder.append(jsonObject.getJSONObject("sys").get("country") + " \uD83C\uDF0F");
        builder.append("\nCity: ");
        builder.append(jsonObject.get("name") + "  \uD83C\uDFD9");
        builder.append("\nTemp: ");
        builder.append(jsonObject.getJSONObject("main").get("temp") + unitsType(user)[0]
                + "  " + cloudType(jsonObject.getJSONArray("weather").getJSONObject(0).getString("main")));
        builder.append("\nWind: ");
        builder.append(jsonObject.getJSONObject("wind").get("speed") + unitsType(user)[1] + "  \uD83D\uDCA8");
        builder.append("\nDate: ");
        builder.append(LocalDate.now() + "  \uD83D\uDCC6");
        return builder.toString();
    }

    public JSONObject jsonWeatherDay(String lat, String lon, User user) {
        String json = getWeatherJsonDay(lat, lon, user);
        return new JSONObject(json);
    }

    public JSONObject jsonWeatherDay(String cityName, User user) {
        String json = getWeatherJsonDay(cityName, user);
        return new JSONObject(json);
    }

//        http://api.openweathermap.org/data/2.5/weather?q=Tashkent&APPID=80590df2ef253590c6d38996f13e8dfa&units=metric

    public String getWeatherJsonDay(String lat, String lon, User user) {
        String url = "https://api.openweathermap.org/data/2.5/weather?"
                + "lat=" + lat
                + "&lon=" + lon
                + "&appid="
                + token
                + "&units="
                + ComponentContainer.userUnitsMap.get(user.getId()).name();
        return getWeatherRequest(url);
    }

    public String getWeatherJsonDay(String cityName, User user) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q="
                + cityName
                + "&appid="
                + token
                + "&units="
                + ComponentContainer.userUnitsMap.get(user.getId()).name();
        return getWeatherRequest(url);
    }

    public String getWeatherJsonWeek(String lat, String lon, User user) {
        String url = "https://api.openweathermap.org/data/2.5/forecast?"
                + "lat=" + lat
                + "&lon=" + lon
                + "&appid="
                + token
                + "&units="
                + ComponentContainer.userUnitsMap.get(user.getId()).name();
        return getWeatherRequest(url);
    }

    public String getWeatherJsonWeek(String cityName, User user) {
        String url = "https://api.openweathermap.org/data/2.5/forecast?q="
                + cityName
                + "&appid="
                + token
                + "&units="
                + ComponentContainer.userUnitsMap.get(user.getId()).name();
        return getWeatherRequest(url);
    }

    private String getWeatherRequest(String url) {
        try {
            Request request = new Request.Builder().url(url).build();
            return Objects.requireNonNull(new OkHttpClient().newCall(request).execute().body()).string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
