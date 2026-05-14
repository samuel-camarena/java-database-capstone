package com.project.back_end.utils;

public enum TimePeriodOfDay {
    AM,
    PM;
    
    public String mapTimePeriodToStringAmOrPm(TimePeriodOfDay dayPeriod) {
        if (dayPeriod == null) return "";

        return dayPeriod.equals(AM) ? "AM" : "PM";
    }
    
    public static TimePeriodOfDay mapStringAmOrPmToTimePeriod(String amOrPm) {
        if (amOrPm == null || amOrPm.isBlank()) return null;
        
        return switch (amOrPm.toLowerCase()) {
            case "am" -> AM;
            case "pm" -> PM;
            default -> null;
        };
    }
}
