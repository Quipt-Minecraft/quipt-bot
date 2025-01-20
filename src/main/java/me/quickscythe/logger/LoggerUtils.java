/*
 * Copyright (c) 2025. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package me.quickscythe.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LoggerUtils {

    private static final Map<String, Logger> LOGGERS = new HashMap<>();

    private static void checkLogger(String tag){
        if(!LOGGERS.containsKey(tag))
            LOGGERS.put(tag, LoggerFactory.getLogger(tag));
    }
    public static void log(String tag, String message, Object... arguments){
        log(tag, LogLevel.INFO, message, arguments);
    }

    public static void log(String tag, String format){
        log(tag, LogLevel.INFO, format);
    }

    public static void log(String tag, String format, Object arg1){
        log(tag, LogLevel.INFO, format, arg1);
    }

    public static void log(String tag, LogLevel level, String message, Object... args) {
        checkLogger(tag);
        switch (level) {
            case INFO:
                LOGGERS.get(tag).info(message, args);
                break;
            case WARN:
                LOGGERS.get(tag).warn(message, args);
                break;
            case ERROR:
                error(tag, message, args);
                break;
            case DEBUG:
                LOGGERS.get(tag).debug(message, args);
                break;

        }
    }

    public static void error(String tag, String message, Object... args) {
        checkLogger(tag);

        LOGGERS.get(tag).error(message, args);
    }

    public static void error(String tag, String message, Throwable throwable, Object... args) {
        message = message.formatted(args);
        checkLogger(tag);

        LOGGERS.get(tag).error(message, throwable);
    }

    public enum LogLevel {
        INFO, WARN, ERROR, DEBUG;
    }

}