package test.commerce;

import java.util.UUID;

public class UsernameGenerator {

    public static String generate() {
        return "username" + UUID.randomUUID();
    }
}
