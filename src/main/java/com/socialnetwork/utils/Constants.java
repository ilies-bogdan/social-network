package com.socialnetwork.utils;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final int MINIMUM_PASSWORD_LENGTH = 8;

    public static final String DATE_TIME_FORMAT_POSTGRESQL = "dd-MM-yyyy HH24:MI";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public static final int SALT_SIZE = 8;

    public static final String ALPHA_NUMERIC_STRINGS = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890";
}
