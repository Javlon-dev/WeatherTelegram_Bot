package com.company.application;

import com.company.container.ComponentContainer;
import com.company.controller.MainController;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;

public class WeatherTelegramBot extends TelegramLongPollingBot {

    private MainController mainController = ComponentContainer.MAIN_CONTROLLER;

    @Override
    public String getBotUsername() {
        return "Weather_byUniDevs_Bot";
    }

    @Override
    public String getBotToken() {
        return "5245103640:AAH1VfS5hOqWydkSgdO34FN5UetxHu1-0uU";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                User user = message.getFrom();
                if (message.hasText()) {
                    log(user, message.getText());
                    mainController.handleText(user, message);
//                } else if (message.hasPhoto()) {
//                    mainController.handlePhotoUpload(user, message);
//                } else if (message.hasVideo()) {
//                    mainController.handleVideoUpload(user, message);
//                } else if (message.hasContact()) {
//                    mainController.handleContact(user, message);
                } else if (message.hasLocation()) {
                    mainController.handleLocation(user, message);
                }
            } else if (update.hasCallbackQuery()) {
                Message message = update.getCallbackQuery().getMessage();
                User user = update.getCallbackQuery().getFrom();
                String data = update.getCallbackQuery().getData();
                log(user, data);
                mainController.handleCallback(user, message, data);
            }
//            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }


    public void send(Object object) {
        try {
            if (object instanceof SendMessage) {
                execute((SendMessage) object);
            } else if (object instanceof EditMessageText) {
                execute((EditMessageText) object);
            } else if (object instanceof SendPhoto) {
                execute((SendPhoto) object);
            } else if (object instanceof SendVideo) {
                execute((SendVideo) object);
            } else if (object instanceof SendContact) {
                execute((SendContact) object);
            } else if (object instanceof SendLocation) {
                execute((SendLocation) object);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void log(User user, String text) {
        String str = String.format(LocalDateTime.now() + ", Userid: %d, Firstname: %s, Text: %s",
                user.getId(), user.getFirstName(), text);
        System.out.println(str);
    }
}
