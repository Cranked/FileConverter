package com.raisedsoftware.util;

import java.util.UUID;

public class KeyCreater {
    public static String randomNumber() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
