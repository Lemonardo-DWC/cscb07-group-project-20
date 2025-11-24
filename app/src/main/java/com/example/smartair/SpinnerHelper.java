package com.example.smartair;

import java.util.ArrayList;

public class SpinnerHelper {

    public ArrayList<String> getYearRange() {

        ArrayList<String> yearList = new ArrayList<String>();

        for(int i = AppConstants.YEARSTART; i <= AppConstants.YEAREND; i++) {
            yearList.add(String.valueOf(i));
        }

        return yearList;
    }

    public ArrayList<String> getMonthRange() {

        ArrayList<String> monthList  = new ArrayList<String>();

        for(int i = 1; i <= 12; i ++) {
            if (i < 10) {
                monthList.add(0 + String.valueOf(i));
            } else {
                monthList.add(String.valueOf(i));
            }
        }

        return monthList;

    }

    public ArrayList<String> getDayRange(String year, String month) {

        ArrayList<String> dayList = new ArrayList<String>();

        switch (month) {
            case "01":
            case "03":
            case "05":
            case "07":
            case "08":
            case "10":
            case "12":
                for(int i = 1; i <= 31; i++) {
                    if (i < 10) {
                        dayList.add(0 + String.valueOf(i));
                    } else {
                        dayList.add(String.valueOf(i));
                    }
                }
                break;

            case "04":
            case "06":
            case "09":
            case "11":
                for(int i = 1; i <= 30; i++) {
                    if (i < 10) {
                        dayList.add(0 + String.valueOf(i));
                    } else {
                        dayList.add(String.valueOf(i));
                    }
                }
                break;

            case "02":
                if(isLeapYear(year)) {
                    for(int i = 1; i <= 29; i++) {
                        if (i < 10) {
                            dayList.add(0 + String.valueOf(i));
                        } else {
                            dayList.add(String.valueOf(i));
                        }
                    }
                } else {
                    for(int i = 1; i <= 28; i++) {
                        if (i < 10) {
                            dayList.add(0 + String.valueOf(i));
                        } else {
                            dayList.add(String.valueOf(i));
                        }
                    }
                }
                break;
        }

        return dayList;

    }

    public int getMaxDayIndex(String year, String month) {
        int maxDayIndex = 0;

        switch (month) {
            case "01":
            case "03":
            case "05":
            case "07":
            case "08":
            case "10":
            case "12":
                maxDayIndex = 30;
                break;

            case "04":
            case "06":
            case "09":
            case "11":
                maxDayIndex = 29;
                break;

            case "02":
                if(isLeapYear(year)) {
                    maxDayIndex = 28;
                } else {
                    maxDayIndex = 27;
                }
                break;
        }

        return maxDayIndex;
    }

    public boolean isLeapYear(String year) {
        int temp = Integer.parseInt(year);
        return (temp % 4 == 0 && temp % 100 != 0) || (temp % 400 == 0);
    }

}
