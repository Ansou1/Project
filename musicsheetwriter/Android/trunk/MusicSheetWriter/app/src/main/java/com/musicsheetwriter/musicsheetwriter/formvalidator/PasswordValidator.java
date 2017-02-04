package com.musicsheetwriter.musicsheetwriter.formvalidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PasswordValidator {

    private Pattern pattern;

    private static final String PASSWORD_VALIDATOR =
            "((?=.*\\d)(?=.*[a-z]).{6,20})";

    public PasswordValidator() {
        pattern = Pattern.compile(PASSWORD_VALIDATOR);
    }

    /**
     * Validate hex with regular expression
     *
     * @param hex
     *            hex for validation
     * @return true valid hex, false invalid hex
     */
    public boolean validate(final String hex) {
        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();

    }
}
