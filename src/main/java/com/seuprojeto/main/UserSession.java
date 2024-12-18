package com.seuprojeto.main;

public class UserSession {

    private static String loggedInUser;

    public static String getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(String loggedInUser) {
        UserSession.loggedInUser = loggedInUser;
    }
}
