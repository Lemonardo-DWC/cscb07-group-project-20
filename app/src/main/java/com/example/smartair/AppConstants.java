package com.example.smartair;

public class AppConstants {

    /// Login error messages
    public static final String INVALID_EMAIL_PASS = "Invalid email/username or password";
    public static final String VERIFY_EMAIL = "Please verify your email first";
    public static final String CANT_LOAD_ACCOUNT_TYPE = "Could not load account type";
    public static final String LOGIN_ERROR = "Login error occurred, please try again";

    /// registration error messages
    public static final String INVALID_EMAIL = "Invalid email";
    public static final String INVALID_PASS = "Password must be at least 6 characters";
    public static final String PASSWORD_MISMATCH = "Passwords do not match";

    ///  result
    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";

    /// database constants
    public static final String USERPATH = "users";
    public static final String USERNAMEPATH = "usernames";
    public static final String EMAIL = "email";
    public static final String ACCOUNTTYPE = "accountType";
    public static final String BASICINFORMATION = "basicInformation";
    public static final String FIRSTNAME = "firstName";
    public static final String MIDDLENAME = "middleName";
    public static final String LASTNAME = "lastName";
    public static final String BIRTHDAY = "birthday";
    public static final String SEX = "sex";
    public static final String PARENTLIST = "parentList";
    public static final String CHILDRENLIST = "childrenList";
    public static final String PARENT = "parent";
    public static final String CHILD = "child";
    public static final String PROVIDER = "healthcare provider";

    /// year range endpoints
    public static final int YEARSTART = 2000;
    public static final int YEAREND = 2030;

    /// biological sex
    public static final String MALE = "male";
    public static final String FEMALE = "female";
    public static final String OTHER = "other";

    /// other constants
    public static final String EMAIL_REGEX
            = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+";

    public static final String NAME_REGEX
            = "^(?=.{1,50}$)[A-Za-zÀ-ÖØ-öø-ÿ]+([.' -][A-Za-zÀ-ÖØ-öø-ÿ]+)*[.'-]?$";

    public static final String SYNTH_EMAIL_DOMAIN = "@smartair.user.app";
}
