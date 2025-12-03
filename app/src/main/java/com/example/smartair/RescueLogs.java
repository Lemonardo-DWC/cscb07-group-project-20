package com.example.smartair;

public class RescueLogs extends AlertItem {

    public int dose;
    public int preBreathRating;
    public int postBreathRating;
    public int postStatus;
    public long timestamp;

    public RescueLogs() {}

    @Override
    public long gettimestamp() {
        return timestamp;
    }

    public String parsePostStatus() {
        String postStatusText = "Same";

        switch (postStatus){
            case -1:
                postStatusText = "Worse";
                break;
            case 1:
                postStatusText = "Better";
                break;
        }

        return postStatusText;
    }

    @Override
    public int getType() {
        return AlertItem.TYPE_RESCUE;
    }
}
