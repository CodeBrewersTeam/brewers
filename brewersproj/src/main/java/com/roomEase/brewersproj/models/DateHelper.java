package com.roomEase.brewersproj.models;


import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;

public class DateHelper {

    public static LocalDate getStartOfWeek(LocalDate date) {

        // this is START of the week. For example: lets say your week starts on Monday then Dayofthe week needs to be Monday
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
    }

    // same as above but for end of the week!!!!
    public static LocalDate getEndOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
    }
}
