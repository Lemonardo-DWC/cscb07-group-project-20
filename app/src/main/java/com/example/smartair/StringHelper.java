package com.example.smartair;

public class StringHelper {

    public String getSyntheticEmail(String username) {
        return username + AppConstants.SYNTH_EMAIL_DOMAIN;
    }

    public String toTitleCase(String string) {

        StringBuilder result = new StringBuilder();

        if (!string.isEmpty()) {
            char[] arr = string.toCharArray();

            result.append(String.valueOf(arr[0]).toUpperCase());

            for(int i = 1; i < string.length(); i ++) {
                result.append(String.valueOf(arr[i]).toLowerCase());
            }
        }

        return result.toString();
    }

}
