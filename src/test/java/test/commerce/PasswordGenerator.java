package test.commerce;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String generate() {
        var mixture = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            mixture.append((char) random.nextInt('A', 'Z' + 1));
            mixture.append((char) random.nextInt('0', '9' + 1));
            mixture.append((char) random.nextInt('A', 'Z' + 1));
        }
        return "Password" + mixture;
    }
}
