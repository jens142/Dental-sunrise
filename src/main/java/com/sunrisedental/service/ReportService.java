package com.sunrisedental.service;

import com.sunrisedental.patterns.factory.Report;
import com.sunrisedental.patterns.factory.ReportFactory;

import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * Thin wrapper around the Factory Method pattern - keeps report
 * selection/validation logic out of the Servlet layer.
 */
public class ReportService {

    public List<Map<String, Object>> runReport(ReportFactory.ReportType type, Date startDate, Date endDate) {
        if (type == null) {
            throw new IllegalArgumentException("Report type is required.");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required.");
        }
        // DailyScheduleReport only needs startDate; other reports need a range
        if (type != ReportFactory.ReportType.DAILY_SCHEDULE && endDate == null) {
            throw new IllegalArgumentException("End date is required for this report type.");
        }
        if (endDate != null && endDate.before(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        Report report = ReportFactory.createReport(type); // Factory Method call site
        return report.generate(startDate, endDate);
    }

    public String getReportTitle(ReportFactory.ReportType type) {
        return ReportFactory.createReport(type).getReportTitle();
    }
}