package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.model.ApiResponse;

import jakarta.servlet.http.HttpSession;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import java.io.ByteArrayOutputStream;

@Service
public class HrService {;
    RestTemplate restTemplate;
    String alldataUrl = "http://erpnext.localhost:8000/api/method/hrms.controllers.api.api_data.get_employee_data";
   public HrService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public AllHRDataDTO getAllHr(HttpSession session){
         String token = (String) session.getAttribute("accessToken");
        String sid = (String) session.getAttribute("sid");

        if (token == null || sid == null) {
            throw new RuntimeException("Token ou SID manquant dans la session.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.add("Cookie", "sid=" + sid);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<ApiResponse<AllHRDataDTO>> response = restTemplate.exchange(
            alldataUrl,
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<ApiResponse<AllHRDataDTO>>() {}
        );

        ApiResponse<AllHRDataDTO> apiResponse = response.getBody();

        if (apiResponse != null && apiResponse.getMessage() != null) {
            return apiResponse.getMessage();
        } else {
            throw new RuntimeException("Erreur lors de la récupération des données.");
        }
    }
    public byte[] generatePayslipPdf(EmployeeDTO employee, SalarySlipDTO salarySlip) {
    if (employee == null || salarySlip == null) {
        throw new IllegalArgumentException("Employee or salary slip cannot be null.");
    }

    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);
        doc.setMargins(20, 20, 20, 20);

        // Company Header
        doc.add(new Paragraph(employee.getCompany())
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("Payslip")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20));

        // Employee Information (Two-column layout)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Table empInfoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
        empInfoTable.setWidth(UnitValue.createPercentValue(100));
        empInfoTable.setMarginBottom(20);

        // Left Column
        Cell leftCell = new Cell();
        leftCell.add(new Paragraph("Date of Joining: " + (employee.getDateOfJoining() != null ? employee.getDateOfJoining().format(formatter) : "None"))
                .setFontSize(10));
        String payPeriod = (salarySlip.getStartDate() != null && salarySlip.getEndDate() != null)
                ? salarySlip.getStartDate().format(formatter) + " - " + salarySlip.getEndDate().format(formatter)
                : "None";
        leftCell.add(new Paragraph("Pay Period: " + payPeriod).setFontSize(10));
        leftCell.add(new Paragraph("Worked Days: 30").setFontSize(10)); // Static as per HTML
        empInfoTable.addCell(leftCell.setVerticalAlignment(VerticalAlignment.MIDDLE));

        // Right Column
        Cell rightCell = new Cell();
        rightCell.add(new Paragraph("Payment-Day: " + (salarySlip.getPayementday() != null ? salarySlip.getPayementday() : "N/A"))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT));
        rightCell.add(new Paragraph("Absent-Day: " + (salarySlip.getAbsentday() != null ? salarySlip.getAbsentday() : "N/A"))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT));
        empInfoTable.addCell(rightCell.setVerticalAlignment(VerticalAlignment.MIDDLE));

        doc.add(empInfoTable);

        // Employee Details
        doc.add(new Paragraph("Employee Name: " + (employee.getEmployeeName() != null ? employee.getEmployeeName() : "N/A"))
                .setFontSize(10));
        doc.add(new Paragraph("Designation: Employee").setFontSize(10)); // Static as per HTML
        doc.add(new Paragraph("Department: " + (employee.getDepartment() != null ? employee.getDepartment() : "None"))
                .setFontSize(10));
        doc.add(new Paragraph("\n"));

        // Earnings Table
        doc.add(new Paragraph("Earnings")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(5));
        Table earningsTable = new Table(UnitValue.createPercentArray(new float[]{75, 25}));
        earningsTable.setWidth(UnitValue.createPercentValue(100));
        earningsTable.setMarginBottom(20);

        // Table Header
        earningsTable.addHeaderCell(new Cell().add(new Paragraph("Description").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        earningsTable.addHeaderCell(new Cell().add(new Paragraph("Amount").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));

        // Table Body
        DecimalFormat df = new DecimalFormat("#,##0.00");
        if (salarySlip.getCorrespond() != null && !salarySlip.getCorrespond().isEmpty()) {
            for (SalaryDetailDTO detail : salarySlip.getCorrespond()) {
                if (detail != null) {
                    earningsTable.addCell(new Cell().add(new Paragraph(detail.getSalaryComponent() != null ? detail.getSalaryComponent() : "N/A")));
                    earningsTable.addCell(new Cell().add(new Paragraph(detail.getAmount() != null ? df.format(detail.getAmount()) + " EUR" : "0.00 EUR"))
                            .setTextAlignment(TextAlignment.RIGHT));
                }
            }
        } else {
            earningsTable.addCell(new Cell(1, 2).add(new Paragraph("No earnings details available.").setTextAlignment(TextAlignment.CENTER)));
        }

        // Total Earnings
        earningsTable.addCell(new Cell().add(new Paragraph("Total Earnings").setBold()));
        earningsTable.addCell(new Cell().add(new Paragraph(salarySlip.getGrossPay() != null ? df.format(salarySlip.getGrossPay()) + ", EUR" : "0.00 EUR"))
                .setTextAlignment(TextAlignment.RIGHT).setBold());

        doc.add(earningsTable);

        // Deductions Table
        doc.add(new Paragraph("Deductions")
                .setFontSize(12)
                .setBold()
                .setMarginBottom(5));
        Table deductionsTable = new Table(UnitValue.createPercentArray(new float[]{75, 25}));
        deductionsTable.setWidth(UnitValue.createPercentValue(100));
        deductionsTable.setMarginBottom(20);

        // Table Header
        deductionsTable.addHeaderCell(new Cell().add(new Paragraph("Description").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        deductionsTable.addHeaderCell(new Cell().add(new Paragraph("Amount").setBold()).setBackgroundColor(ColorConstants.LIGHT_GRAY));

        // Table Body
        if (salarySlip.getDeductions() != null && !salarySlip.getDeductions().isEmpty()) {
            for (SalaryDetailDTO deduct : salarySlip.getDeductions()) {
                if (deduct != null) {
                    deductionsTable.addCell(new Cell().add(new Paragraph(deduct.getSalaryComponent() != null ? deduct.getSalaryComponent() : "N/A")));
                    deductionsTable.addCell(new Cell().add(new Paragraph(deduct.getAmount() != null ? df.format(deduct.getAmount()) + " EUR" : "0.00 EUR"))
                            .setTextAlignment(TextAlignment.RIGHT));
                }
            }
        } else {
            deductionsTable.addCell(new Cell(1, 2).add(new Paragraph("No deductions available.").setTextAlignment(TextAlignment.CENTER)));
        }

        // Total Deductions
        deductionsTable.addCell(new Cell().add(new Paragraph("Total Deductions").setBold()));
        deductionsTable.addCell(new Cell().add(new Paragraph(salarySlip.getTotalDeduction() != null ? df.format(salarySlip.getTotalDeduction()) + ", EUR" : "0.00 EUR"))
                .setTextAlignment(TextAlignment.RIGHT).setBold());

        doc.add(deductionsTable);

        // Net Pay
        doc.add(new Paragraph("Net Pay")
                .setFontSize(14)
                .setBold()
                .setMarginBottom(5));
        doc.add(new Paragraph(salarySlip.getNetPay() != null ? df.format(salarySlip.getNetPay()) + ", EUR" : "0.00 EUR")
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT));
        String netPayInWords = numberToWords(salarySlip.getNetPay() != null ? salarySlip.getNetPay().longValue() : 0) + " EUR";
        doc.add(new Paragraph(netPayInWords)
                .setFontSize(10)
                .setItalic()
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginBottom(20));

        // Footer
        doc.add(new Paragraph("This is a system-generated payslip")
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY));

        doc.close();
        System.out.println("PDF generated successfully for slip: " + salarySlip.getName());
        return baos.toByteArray();
    } catch (Exception e) {
        System.err.println("Error generating PDF for slip: " + salarySlip.getName() + ": " + e.getMessage());
        throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
    }
}

// Helper method to convert number to words (English, simplified for EUR)
private String numberToWords(long number) {
    if (number == 0) return "Zero";

    String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
    String[] teens = {"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
    String[] tens = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
    String[] thousands = {"", "Thousand", "Million"};

    StringBuilder words = new StringBuilder();
    int groupIndex = 0;

    while (number > 0) {
        int group = (int) (number % 1000);
        if (group > 0) {
            String groupWords = "";
            if (group >= 100) {
                groupWords += units[group / 100] + " Hundred ";
                group %= 100;
            }
            if (group >= 20) {
                groupWords += tens[group / 10] + " ";
                group %= 10;
            }
            if (group >= 10 && group < 20) {
                groupWords += teens[group - 10] + " ";
                group = 0;
            }
            if (group > 0 && group < 10) {
                groupWords += units[group] + " ";
            }
            words.insert(0, groupWords + thousands[groupIndex] + " ");
        }
        number /= 1000;
        groupIndex++;
    }

    return words.toString().trim();
}
}
