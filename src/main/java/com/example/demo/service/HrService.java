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
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

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

            // Company Header
            doc.add(new Paragraph(employee.getCompany())
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT));
            doc.add(new Paragraph("Payslip")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10)
                .setMarginBottom(20));

            // Employee Information
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            doc.add(new Paragraph("Date of Joining: " + (employee.getDateOfJoining() != null ? employee.getDateOfJoining().format(formatter) : "Unknown"))
                .setFontSize(10));
            String payPeriod = salarySlip.getStartDate() != null && salarySlip.getEndDate() != null
                ? salarySlip.getStartDate().format(formatter) + " - " + salarySlip.getEndDate().format(formatter)
                : "Not specified";
            doc.add(new Paragraph("Pay Period: " + payPeriod)
                .setFontSize(10));
            doc.add(new Paragraph("Worked Days: 30") // Assumption for June 2025
                .setFontSize(10));
            doc.add(new Paragraph("\n"));
            doc.add(new Paragraph("Employee Name: " + (employee.getEmployeeName() != null ? employee.getEmployeeName() : "N/A"))
                .setFontSize(10));
            doc.add(new Paragraph("Designation: Employee") // Placeholder
                .setFontSize(10));
            doc.add(new Paragraph("Department: "+ (employee.getDepartment() != null ? employee.getDepartment() : "N/A")) // Placeholder
                .setFontSize(10));
            doc.add(new Paragraph("\n"));
            doc.add(new Paragraph("Payment-Day: "+(salarySlip.getPayementday() != null ? salarySlip.getPayementday() : "N/A"))
                .setFontSize(10));
            doc.add(new Paragraph("Absent-Day: "+(salarySlip.getAbsentday() != null ? salarySlip.getAbsentday() : "N/A"))
                .setFontSize(10));
            // Earnings Table
            DecimalFormat df = new DecimalFormat("#,##0.00 EUR");
            Table earningsTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
            earningsTable.setWidth(UnitValue.createPercentValue(50));
            earningsTable.setMarginBottom(10);

            earningsTable.addHeaderCell("Earnings").setBackgroundColor(ColorConstants.LIGHT_GRAY);
            earningsTable.addHeaderCell("Amount").setBackgroundColor(ColorConstants.LIGHT_GRAY);
            earningsTable.addCell("Basic Pay");
            earningsTable.addCell(df.format(salarySlip.getGrossPay() != null ? salarySlip.getGrossPay() : 0.0));
            earningsTable.addCell("Incentive Pay");
            earningsTable.addCell(df.format(0.0)); // Placeholder
            earningsTable.addCell("House Rent Allowance");
            earningsTable.addCell(df.format(0.0)); // Placeholder
            earningsTable.addCell("Meal Allowance");
            earningsTable.addCell(df.format(0.0)); // Placeholder
            earningsTable.addCell("Total Earnings");
            earningsTable.addCell(df.format(salarySlip.getGrossPay() != null ? salarySlip.getGrossPay() : 0.0)).setBold();

            doc.add(earningsTable);

            // Deductions Table
            Table deductionsTable = new Table(UnitValue.createPercentArray(new float[]{3, 1}));
            deductionsTable.setWidth(UnitValue.createPercentValue(50));
            deductionsTable.setMarginBottom(20);

            deductionsTable.addHeaderCell("Deductions").setBackgroundColor(ColorConstants.LIGHT_GRAY);
            deductionsTable.addHeaderCell("Amount").setBackgroundColor(ColorConstants.LIGHT_GRAY);
            deductionsTable.addCell("Provident Fund");
            deductionsTable.addCell(df.format(0.0)); // Placeholder
            deductionsTable.addCell("Professional Tax");
            deductionsTable.addCell(df.format(0.0)); // Placeholder
            deductionsTable.addCell("Loan");
            deductionsTable.addCell(df.format(0.0)); // Placeholder
            deductionsTable.addCell("Other Deductions");
            deductionsTable.addCell(df.format(salarySlip.getTotalDeduction() != null ? salarySlip.getTotalDeduction() : 0.0));
            deductionsTable.addCell("Total Deductions");
            deductionsTable.addCell(df.format(salarySlip.getTotalDeduction() != null ? salarySlip.getTotalDeduction() : 0.0)).setBold();

            doc.add(deductionsTable);

            // Net Pay
            doc.add(new Paragraph("Net Pay: " + df.format(salarySlip.getNetPay() != null ? salarySlip.getNetPay() : 0.0))
                .setFontSize(12)
                .setBold()
                .setMarginBottom(10));

            // Net Pay in Words
            String netPayInWords = numberToWords(salarySlip.getNetPay() != null ? salarySlip.getNetPay().longValue() : 0) + " Euros";
            doc.add(new Paragraph(netPayInWords)
                .setFontSize(10)
                .setItalic()
                .setMarginBottom(20));

            // Footer
            doc.add(new Paragraph("\n"));
            doc.add(new Paragraph("This is system generated payslip")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER));

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
