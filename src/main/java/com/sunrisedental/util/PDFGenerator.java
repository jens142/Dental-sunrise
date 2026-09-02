package com.sunrisedental.util;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.TextAlignment;
import com.sunrisedental.model.Bill;

import java.io.ByteArrayOutputStream;

/**
 * Generates a print-friendly PDF receipt for a Bill.
 * Called by BillingServlet's /print endpoint - keeps PDF layout
 * concerns out of the Servlet and Service layers entirely.
 */
public class PDFGenerator {

    public static byte[] generateReceipt(Bill bill) {
        return generateReceipt(bill, null);
    }

    public static byte[] generateReceipt(Bill bill, byte[] logoBytes) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        try {
                DeviceRgb primary = new DeviceRgb(0, 93, 144);
                DeviceRgb lightBlue = new DeviceRgb(232, 244, 250);
                DeviceRgb muted = new DeviceRgb(90, 103, 114);

                Table header = new Table(UnitValue.createPercentArray(new float[]{1, 2.8f}))
                    .useAllAvailableWidth()
                    .setBorder(Border.NO_BORDER)
                    .setMarginBottom(18);
                if (logoBytes != null && logoBytes.length > 0) {
                Image logo = new Image(ImageDataFactory.create(logoBytes))
                    .scaleToFit(86, 86)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);
                header.addCell(new Cell().add(logo).setBorder(Border.NO_BORDER));
                } else {
                header.addCell(new Cell().setBorder(Border.NO_BORDER));
                }
                Cell clinicCell = new Cell()
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                    .add(new Paragraph("SUNRISE DENTAL CLINIC").setFontSize(18).setBold().setFontColor(primary))
                    .add(new Paragraph("Professional dental care with a brighter smile").setFontSize(9).setFontColor(muted))
                    .add(new Paragraph("Colombo, Sri Lanka  |  077 123 4567  |  info@sunrisedental.lk").setFontSize(8).setFontColor(muted));
                header.addCell(clinicCell);
                document.add(header);

                document.add(new Paragraph("PAYMENT RECEIPT")
                    .setFontSize(15)
                    .setBold()
                    .setFontColor(ColorConstants.WHITE)
                    .setBackgroundColor(primary)
                    .setPadding(10)
                    .setTextAlignment(TextAlignment.CENTER));

                Table details = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .useAllAvailableWidth()
                    .setMarginTop(16)
                    .setMarginBottom(18);
                addDetail(details, "Receipt No.", "BILL-" + bill.getBillId(), primary);
                addDetail(details, "Appointment No.", safe(bill.getAppointmentNumber()), primary);
                addDetail(details, "Patient", safe(bill.getPatientName()), primary);
                addDetail(details, "Dentist", safe(bill.getDentistName()), primary);
                addDetail(details, "Treatment", safe(bill.getTreatmentName()), primary);
                addDetail(details, "Issued", bill.getGeneratedOn() != null ? bill.getGeneratedOn().toString() : "-", primary);
                document.add(details);

                Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1.2f}))
                    .useAllAvailableWidth()
                    .setMarginBottom(14);
                addHeaderCell(table, "DESCRIPTION", primary);
                addHeaderCell(table, "AMOUNT", primary);
                addLineItem(table, "Consultation Fee", bill.getConsultationFee(), lightBlue, false);
                addLineItem(table, "Treatment: " + safe(bill.getTreatmentName()), bill.getTreatmentCost(), lightBlue, false);
                addLineItem(table, "Discount", bill.getDiscount(), lightBlue, false);
                addLineItem(table, "TOTAL AMOUNT", bill.getTotalAmount(), primary, true);
            document.add(table);

                document.add(new Paragraph("PAYMENT STATUS: " + bill.getPaymentStatus())
                    .setBold()
                    .setFontColor(primary)
                    .setBorder(new SolidBorder(primary, 1))
                    .setPadding(9)
                    .setTextAlignment(TextAlignment.CENTER));

                document.add(new Paragraph("Thank you for choosing Sunrise Dental Clinic.")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(9)
                    .setFontColor(muted)
                    .setMarginTop(24));

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF receipt", e);
        } finally {
            // Closing flushes all buffered content into outputStream.
            // This MUST happen before toByteArray() is called below,
            // which is exactly why this isn't a try-with-resources block -
            // resource close there happens after the return value is
            // already evaluated, producing an empty/corrupt PDF.
            document.close();
        }

        return outputStream.toByteArray();
    }

    private static String formatAmount(java.math.BigDecimal amount) {
        return amount != null ? "Rs. " + amount.setScale(2, java.math.RoundingMode.HALF_UP) : "Rs. 0.00";
    }

    private static String safe(String value) {
        return value != null && !value.isBlank() ? value : "-";
    }

    private static void addDetail(Table table, String label, String value, DeviceRgb primary) {
        table.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(5)
                .add(new Paragraph(label).setFontSize(8).setFontColor(primary).setBold()));
        table.addCell(new Cell().setBorder(Border.NO_BORDER).setPadding(5)
                .add(new Paragraph(value).setFontSize(9)));
    }

    private static void addHeaderCell(Table table, String text, DeviceRgb primary) {
        table.addHeaderCell(new Cell().setBackgroundColor(primary).setBorder(Border.NO_BORDER).setPadding(8)
                .add(new Paragraph(text).setFontSize(8).setBold().setFontColor(ColorConstants.WHITE)));
    }

    private static void addLineItem(Table table, String label, java.math.BigDecimal amount,
                                    DeviceRgb background, boolean total) {
        Paragraph labelParagraph = new Paragraph(label).setFontSize(total ? 10 : 9);
        Paragraph amountParagraph = new Paragraph(formatAmount(amount)).setFontSize(total ? 11 : 9)
            .setTextAlignment(TextAlignment.RIGHT);
        if (total) {
            labelParagraph.setBold();
            amountParagraph.setBold();
        }
        Cell labelCell = new Cell().setBackgroundColor(background).setPadding(9).setBorder(Border.NO_BORDER)
            .add(labelParagraph);
        Cell amountCell = new Cell().setBackgroundColor(background).setPadding(9).setBorder(Border.NO_BORDER)
            .add(amountParagraph);
        table.addCell(labelCell);
        table.addCell(amountCell);
    }
}