package main.java.enums.system;

import java.util.regex.Pattern;

public enum RegexPattern {
    ANY_UPPERCASE(Pattern.compile(".*[A-Z].*")),
    ANY_LOWERCASE(Pattern.compile(".*[a-z].*")),
    ANY_SPECIALCHAR(Pattern.compile(".*[!@#$%^&*(),.?:{}|<>].*")),
    IC_PATTERN(Pattern.compile("^\\d{12}$")),
    PASSPORT_PATTERN(Pattern.compile("^[A-Z]{1,2}[0-9]{6,9}$")),
    EMAIL_PATTERN(Pattern.compile("^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$")),
    ANY_NUMBER(Pattern.compile(".*[0-9].*")),
    PHONE_PATTERN(Pattern.compile("^0[0-9]{8,10}$")),
    UUID_PATTERN(Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"));

    private final Pattern regexPattern;

    RegexPattern(Pattern regexPattern) {
        this.regexPattern = regexPattern;
    }

    // Convenience method - hides the unwrapping from callers
    // Used in validating user input with regex pattern
    public boolean matches(String input) {
        if (input == null) {
            return false;
        }

        return regexPattern.matcher(input).matches();
    }
}
