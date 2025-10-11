package test.commerce;

public class TestDataSource {

    public static String[] invalidPasswords() {
        return new String[] { "", "pass", "pass123", "1234password", "password1234", "pass5678word" };
    }
}
