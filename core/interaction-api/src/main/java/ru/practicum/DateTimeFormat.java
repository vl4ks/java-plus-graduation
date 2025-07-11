package ru.practicum;

import java.time.format.DateTimeFormatter;

public class DateTimeFormat {
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
}
