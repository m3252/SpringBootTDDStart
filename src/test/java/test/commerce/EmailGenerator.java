package test.commerce;

import java.util.UUID;

public class EmailGenerator {

    public static String generate() {
        return UUID.randomUUID() + "@test.com";
    }
}
