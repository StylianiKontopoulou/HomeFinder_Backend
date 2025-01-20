package gr.codingfactory.rest.homefinder.services;

import java.util.regex.Pattern;

/*
*  We want the input the user gives to be of a certain format,
*  so we use regex to ensure that this stays true
* */
public class PatternService {

    public static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9.!#$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");

    // For a strong password the input needs to be at least 4 characters including letters both lower case and
    // upper case, numbers and symbols
    public static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,20}$");

    // The date pattern should be of the format yyyy-MM-dd
    public static final Pattern DATE_PATTERN =
            Pattern.compile("^[0-9]{4}+-+[0-9]{1,2}+-+[0-9]{1,2}$");

    public static final Pattern VAT_PATTERN =
            Pattern.compile("^[0-9]{9}$");

    public static final Pattern PHONE_NUMBER_PATTERN =
            Pattern.compile("^[0-9]*$");
}
