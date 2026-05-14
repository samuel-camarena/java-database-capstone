package com.project.back_end.models;

import com.project.back_end.utils.TimePeriodOfDay;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import static com.project.back_end.config.EntityConstraintsConfig.AVAILABLE_TIMES_NOT_BLANK_MSG;

public class TimeSlot {
    public static final String AM =  "AM";
    public static final String PM = "PM";
    
    public static boolean isTimeSlotAvailable(String timeSlot, List<String> availableTimeSlots) {
        return availableTimeSlots
            .stream()
            .anyMatch(tSlot ->
                extractStartingTimeFromTimeSlot(tSlot)
                    .contentEquals(extractStartingTimeFromTimeSlot(timeSlot)));
    }
    
    public static boolean isTimeSlotAtThisTimePeriodOfDay(String timeSlot, TimePeriodOfDay amOrPm) {
        if (amOrPm.equals(TimePeriodOfDay.AM)) {
            return isTimeSlotAtAM(timeSlot);
        } else {
            return isTimeSlotAtPM(timeSlot);
        }
    }
    
    public static boolean isTimeSlotAtAM(String timeSlot) throws IllegalArgumentException, DateTimeParseException {
        if (timeSlot == null || timeSlot.isBlank())
            throw new IllegalArgumentException(AVAILABLE_TIMES_NOT_BLANK_MSG);
        
        return isLocalTimeAtAM(parseTimeSlotToLocalTime(timeSlot));
    }
    
    public static boolean isTimeSlotAtPM(String timeSlot) throws IllegalArgumentException, DateTimeParseException {
        if (timeSlot == null || timeSlot.isBlank())
            throw new IllegalArgumentException(AVAILABLE_TIMES_NOT_BLANK_MSG);
        
        return !isLocalTimeAtAM(parseTimeSlotToLocalTime(timeSlot));
    }
    
    public static boolean isLocalTimeAtAM(LocalTime time) {
        return time.isBefore(LocalTime.NOON);
    }
    
    /**
     * Method xyz
     * @param timeSlot Expected time format as String: "11:00-12:00".
     * @return localTime formated as ISO_LOCAL_TIME: 11:00.
     * @throws DateTimeParseException if the availableTime does not match the expected custom time format.
     */
    public static LocalTime parseTimeSlotToLocalTime(String timeSlot) throws DateTimeParseException {
        String startingTime = extractStartingTimeFromTimeSlot(timeSlot);
        return LocalTime.parse(startingTime, DateTimeFormatter.ISO_LOCAL_TIME);
    }
    
    /**
     * Method to extract a String representing the starting hour of a time slot from an Appointment or
     * Doctor's Available Times.
     * @param timeSlot Expected time format as String: "10:00-11:00" -> "10:00"
     * @return time slot as String: e.g. "10:00"
     */
    public static String extractStartingTimeFromTimeSlot(String timeSlot) {
        return timeSlot.split("-")[0]; // "10:00-11:00" -> "10:00"
    }
    
    /**
     * Method to extract a String representing the ending hour of a time slot from an Appointment or
     * Doctor's Available Times.
     * @param timeSlot Expected time format as String: "10:00-11:00" -> "11:00"
     * @return time slot as String: e.g. "11:00"
     */
    public static String extractEndingTimeFromTimeSlot(String timeSlot) {
        return timeSlot.split("-")[1]; // "10:00-11:00" -> "11:00"
    }
    
}
