package com.hackademics.util;

import java.time.LocalDateTime;

public class TermDeterminator {
    
    /**
     * Determines the current term based on the current date.
     * Terms are determined as follows:
     * - September to December: Next year's Winter term (YYYY1)
     * - January to April: Current year's Spring term (YYYY2)
     * - May to August: Current year's Spring term (YYYY2)
     * 
     * @return The term in format YYYY1 (Winter) or YYYY2 (Spring)
     */
    public static String determineCurrentTerm() {
        return determineTerm(LocalDateTime.now());
    }

    /**
     * Determines the term for a given date.
     * 
     * @param date The date to determine the term for
     * @return The term in format YYYY1 (Winter) or YYYY2 (Spring)
     */
    public static String determineTerm(LocalDateTime date) {
        return switch (date.getMonth()) {
            case SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER ->
                date.getYear() + 1 + "1";
            case JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST ->
                date.getYear() + "2";
        };
    }

    /**
     * Validates if a given term string is in the correct format.
     * 
     * @param term The term to validate
     * @return true if the term is valid, false otherwise
     */
    public static boolean isValidTermFormat(String term) {
        return term.matches("\\d{4}[12]");
    }
} 