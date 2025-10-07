package test.commerce;

import java.util.UUID;

public class PasswordGenerator {

    public static String generate() {
        return "Password" + UUID.randomUUID();
    }
}
