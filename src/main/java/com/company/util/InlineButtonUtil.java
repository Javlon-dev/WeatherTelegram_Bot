package com.company.util;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class InlineButtonUtil {

    public static InlineKeyboardButton button(String text, String callBackData) {
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callBackData);
        return button;
    }

    public static InlineKeyboardButton button(String text, String callBackData, String emoji) {
        String emojiText = EmojiParser.parseToUnicode(emoji + " " + text);
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(emojiText);
        button.setCallbackData(callBackData);
        return button;
    }

    public static List<InlineKeyboardButton> row(InlineKeyboardButton... inlineKeyboardButtons) {
        return new LinkedList<>(Arrays.asList(inlineKeyboardButtons));
    }

    @SafeVarargs
    public static List<List<InlineKeyboardButton>> rowList(List<InlineKeyboardButton>... rows) {
        return new LinkedList<>(Arrays.asList(rows));
    }

    public static InlineKeyboardMarkup keyboard(List<List<InlineKeyboardButton>> rowList) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(rowList);
        return keyboardMarkup;
    }

    /**
     * Utils Keyboards
     */
    public static InlineKeyboardMarkup singleKeyboard() {
        return InlineButtonUtil.keyboard(
                InlineButtonUtil.rowList(
                        InlineButtonUtil.row(
                                InlineButtonUtil.button("Let's go", "/menu", "\uD83C\uDF24")
                        )));
    }

    public static InlineKeyboardMarkup menuKeyboard() {
        return InlineButtonUtil.keyboard(
                InlineButtonUtil.rowList(
                        InlineButtonUtil.row(
                                InlineButtonUtil.button("Weather/day", "/weather/day", "➊\uD83C\uDF24"),
                                InlineButtonUtil.button("Weather/week", "/weather/week", "➐\uD83C\uDF24")
                        ),
                        InlineButtonUtil.row(
                                InlineButtonUtil.button("Weather/settings", "/units", "⚙️\uD83C\uDF24"),
                                InlineButtonUtil.button("Weather/admin", "/contact", "\uD83D\uDC64\uD83C\uDF24")
                        )
                ));
    }

    public static InlineKeyboardMarkup unitsKeyboard() {
        return InlineButtonUtil.keyboard(
                InlineButtonUtil.rowList(
                        InlineButtonUtil.row(
                                InlineButtonUtil.button("Kelvin", "/units/standard", "°K"),
                                InlineButtonUtil.button("Celsius", "/units/metric", "°C"),
                                InlineButtonUtil.button("Fahrenheit", "/units/imperial", "°F")

                )));
    }
}
