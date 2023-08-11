package com.roomEase.brewersproj.models;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;

public class DateHelper {

    public static LocalDate getStartOfWeek(LocalDate date) {

        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
    }

    public static LocalDate getEndOfWeek(LocalDate date) {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
    }
}
