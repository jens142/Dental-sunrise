package com.sunrisedental.patterns.factory;

import java.sql.Date;
import java.util.List;
import java.util.Map;

/**
 * Factory Method Pattern - Product Interface
 * ---------------------------------------------
 * All report types implement this common interface, so the ReportService
 * can work with any report polymorphically without knowing its concrete type.
 */
public interface Report {
    List<Map<String, Object>> generate(Date startDate, Date endDate);
    String getReportTitle();
}