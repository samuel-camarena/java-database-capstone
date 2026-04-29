package com.project.back_end.utils;

public enum TimePeriodOfDay {
    AM,
    PM;

    public boolean isAtThisTimeOfDay(String availableTime) {
        if (availableTime == null || availableTime.isBlank())
            throw new IllegalArgumentException("Available time must not be null nor blank");
        
        boolean isAtAm = Integer.parseInt(availableTime.substring(0, 2)) < 12;
        if (this.equals(AM)) { // availableTime -> 11:00-12:00 -> substring(0, 2) = 11
            return isAtAm;
        } else {
            return !isAtAm;
        }
    }
    
    public static TimePeriodOfDay mapToTimePeriod(String timePeriod) {
        if (timePeriod == null || timePeriod.isBlank()) {
            return null;
        }
        return (AM.toString().equalsIgnoreCase(timePeriod)) ? AM : PM;
    }
}
