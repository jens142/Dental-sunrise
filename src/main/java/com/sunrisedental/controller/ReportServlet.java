package com.sunrisedental.controller;

import com.sunrisedental.patterns.factory.ReportFactory;
import com.sunrisedental.service.ReportService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

// Note: AuthenticationFilter restricts this path to ADMIN role only
@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

    private final ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String typeParam = req.getParameter("type");
        String startParam = req.getParameter("startDate");
        String endParam = req.getParameter("endDate");

        if (typeParam == null) {
            typeParam = "DAILY_SCHEDULE";
            startParam = new Date(System.currentTimeMillis()).toString();
            endParam = startParam;
        }

        req.setAttribute("reportStartDate", startParam);
        req.setAttribute("reportEndDate", endParam);

        try {
            ReportFactory.ReportType type = ReportFactory.ReportType.valueOf(typeParam.toUpperCase());
            Date startDate = Date.valueOf(startParam);
            Date endDate = (endParam != null && !endParam.isBlank()) ? Date.valueOf(endParam) : null;

            List<Map<String, Object>> results = reportService.runReport(type, startDate, endDate);

            req.setAttribute("reportTitle", reportService.getReportTitle(type));
            req.setAttribute("reportData", results);

        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
        }

        req.getRequestDispatcher("/reports.jsp").forward(req, resp);
    }
}