package commerce;

public class UserPropertyValidator {

    public static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_-]{3,}$";

    public static boolean isEmailValid(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }

    public static boolean isUsernameValid(String username) {
        return username != null && username.matches(USERNAME_REGEX);
    }

    public static boolean isPasswordValid(String password) {
        return password != null
            && password.length() >= 8
            && !contains4SequentialChars(password);

    }

    private static boolean contains4SequentialChars(String password) {
        for (int i = 0; i < password.length(); i++) {
            if (i + 3 < password.length()) {
                char c1 = password.charAt(i);
                char c2 = password.charAt(i + 1);
                char c3 = password.charAt(i + 2);
                char c4 = password.charAt(i + 3);
                if ((c1 + 1 == c2 && c2 + 1 == c3 && c3 + 1 == c4) || // ascending
                    (c1 - 1 == c2 && c2 - 1 == c3 && c3 - 1 == c4)) { // descending
                    return true;
                }
            }
        }
        return false;
    }
}
