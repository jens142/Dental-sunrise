package com.sunrisedental.patterns.factory;

/**
 * Factory Method Pattern - Creator
 * -----------------------------------
 * Centralizes the creation logic for Report objects. If a new report
 * type is added later (e.g. PatientHistoryReport), only this factory
 * needs to be updated - client code (ReportService/Servlet) is unaffected.
 */
public class ReportFactory {

    public enum ReportType {
        REVENUE, NO_SHOW, DAILY_SCHEDULE
    }

    public static Report createReport(ReportType type) {
        switch (type) {
            case REVENUE:
                return new RevenueReport();
            case NO_SHOW:
                return new NoShowReport();
            case DAILY_SCHEDULE:
                return new DailyScheduleReport();
            default:
                throw new IllegalArgumentException("Unknown report type: " + type);
        }
    }
}