package utils;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String DATE_TIME_FORMAT_POSTGRESQL = "dd-MM-yyyy HH24:MI";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    // public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static final int SALT_SIZE = 8;

    public static final String ALPHA_NUMERIC_STRINGS = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890";
}
