package com.company.controller;

import com.company.util.InlineButtonUtil;
import com.company.container.ComponentContainer;
import com.company.enums.UnitsStatus;
import com.company.enums.UserStatus;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {

    private Map<Long, User> userMap = new HashMap<>();

    public void handleText(User user, Message message) {
        String text = message.getText();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));
        if (text.equals("/start") || text.equals("start")) {
            StringBuilder builder = new StringBuilder();
            builder.append("Welcome <b>");
            builder.append(user.getFirstName());
            builder.append("</b>\n");
            builder.append("<a href=\"https://t.me/Weather_byUniDevs_Bot\">This bot</a>");
            builder.append(" is ready to work!");

            sendMessage.setParseMode("HTML");
            sendMessage.setDisableWebPagePreview(true);
            sendMessage.setText(builder.toString());
            sendMessage.setReplyMarkup(InlineButtonUtil.singleKeyboard());

            ComponentContainer.userUnitsMap.put(user.getId(), UnitsStatus.standard);
            ComponentContainer.userStatusMap.put(user.getId(), UserStatus.ShowWeatherDay);
            ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
            if (!userMap.containsKey(user.getId())) {
                sendMessage = new SendMessage();
                sendMessage.setChatId(ComponentContainer.ADMIN_ID);

                builder = new StringBuilder();

                builder.append("<a href=\"https://t.me/" + user.getUserName() + "\">" + user.getFirstName() + "</a>");
                builder.append(" joined the bot");

                sendMessage.setParseMode("HTML");
                sendMessage.setDisableWebPagePreview(true);
                sendMessage.setText(builder.toString());

                userMap.put(user.getId(), user);
                ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
            }
        } else if (text.equals("/location")) {
            KeyboardButton button = new KeyboardButton("Send Location");
            button.setRequestLocation(true);
            KeyboardRow row = new KeyboardRow();
            row.add(button);

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setResizeKeyboard(true);
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setKeyboard(List.of(row));

            sendMessage.setText("Location");
            sendMessage.setReplyMarkup(replyKeyboardMarkup);

            ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
        } else if (ComponentContainer.userStatusMap.containsKey(user.getId())) {
            switch (ComponentContainer.userStatusMap.get(user.getId())) {
                case SendMessageToAdmin -> {
                    adminMessage(user, message);
                    ComponentContainer.userStatusMap.put(user.getId(), UserStatus.ShowWeatherDay);
                }
                case ShowWeatherDay, ShowWeatherWeek -> ComponentContainer.WEATHER_CONTROLLER.handleText(user, message);
                case SimpleUser -> autoMsg(sendMessage);
            }
        } else {
            autoMsg(sendMessage);
        }
    }

    private void autoMsg(SendMessage sendMessage) {
        sendMessage.setText("\uD83D\uDE41");
        ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
        sendMessage.setText("Wrong Command");
        ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
    }

    public void handleCallback(User user, Message message, String callback) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setChatId(String.valueOf(user.getId()));

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));

        if (callback.equals("/menu")) {
            sendMessage.setText("Where do we start  <b>" + user.getFirstName() + "</b>");
            sendMessage.setParseMode("HTML");
            sendMessage.setReplyMarkup(InlineButtonUtil.menuKeyboard());

            ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
        } else if (callback.equals("/contact")) {
            sendMessage.setText("Enter your text: ");
            ComponentContainer.userStatusMap.put(user.getId(), UserStatus.SendMessageToAdmin);
            ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
        } else if (callback.startsWith("/units")) {
            switch (callback) {
                case "/units/standard" -> ComponentContainer.userUnitsMap.put(user.getId(), UnitsStatus.standard);
                case "/units/metric" -> ComponentContainer.userUnitsMap.put(user.getId(), UnitsStatus.metric);
                case "/units/imperial" -> ComponentContainer.userUnitsMap.put(user.getId(), UnitsStatus.imperial);
                default -> {
                    editMessageText.setText("OK, choose what you want");
                    editMessageText.setReplyMarkup(InlineButtonUtil.unitsKeyboard());
                    ComponentContainer.WEATHER_TELEGRAM_BOT.send(editMessageText);
                    return;
                }
            }
            editMessageText.setText("Accepted");
            editMessageText.setReplyMarkup(InlineButtonUtil.menuKeyboard());
            ComponentContainer.WEATHER_TELEGRAM_BOT.send(editMessageText);
        } else if (callback.startsWith("/weather")) {
            if (callback.equals("/weather/day")) {
                ComponentContainer.userStatusMap.put(user.getId(), UserStatus.ShowWeatherDay);
            } else if (callback.equals("/weather/week")) {
                ComponentContainer.userStatusMap.put(user.getId(), UserStatus.ShowWeatherWeek);
            }
            sendMessage.setText("Enter your city (in English) or send your Location: ");
            ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
        }

    }


    public void handleLocation(User user, Message message) {
        Location location = message.getLocation();
//        System.out.println(user.getFirstName() + ": " + location.getLatitude() + " " + location.getLongitude());

        SendMessage toAdmin = new SendMessage();
        toAdmin.setChatId(ComponentContainer.ADMIN_ID);
        toAdmin.setText("User: " + "@" + user.getUserName() + " :  " + user.getFirstName() +
                "\n<a href=\"https://maps.google.com/?q=" + location.getLatitude() + "," +
                location.getLongitude() + "\">Location</a>");
        toAdmin.setParseMode("HTML");
        toAdmin.setDisableWebPagePreview(true);

        ComponentContainer.WEATHER_TELEGRAM_BOT.send(toAdmin);

        SendLocation sendLocation = new SendLocation();
        sendLocation.setChatId(ComponentContainer.ADMIN_ID);
        sendLocation.setLatitude(location.getLatitude());
        sendLocation.setLongitude(location.getLongitude());

        ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendLocation);

        ComponentContainer.WEATHER_CONTROLLER.handleText(user, message);
    }

    private void adminMessage(User user, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(user.getId()));
        sendMessage.setText("Accepted");
        ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
        sendMessage.setText("\uD83D\uDE01");
        ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
        sendMessage.setText("We are back again  <b>" + user.getFirstName() + "</b>");
        sendMessage.setParseMode("HTML");
        sendMessage.setReplyMarkup(InlineButtonUtil.menuKeyboard());
        messageToAdmin(user, message);
        ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
    }

    private void messageToAdmin(User user, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(ComponentContainer.ADMIN_ID);
        sendMessage.setText("User:  " + "<a href=\"https://t.me/" + user.getUserName() + "\">" + user.getFirstName() +
                "</a>" + "\nID: " + user.getId() +
                "\nMessage: " + message.getText());
        sendMessage.setDisableWebPagePreview(true);
        sendMessage.setParseMode("HTML");
        ComponentContainer.WEATHER_TELEGRAM_BOT.send(sendMessage);
    }


}
