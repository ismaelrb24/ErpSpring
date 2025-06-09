package com.example.demo.controller;
import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.service.*;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/Hr")
public class HRController {
    @GetMapping("/employeeList")
    public String showEmployeePage(Model model,HttpSession session,RestTemplate restTemplate) {
        String token = (String) session.getAttribute("accessToken");

        if (token == null) {
            return "redirect:/api/auth/";
        }
        else{    
            HrService rhservice = new HrService(restTemplate);
            List<EmployeeDTO> employlist = rhservice.getAllHr(session).getEmployees();
            List<GenderDTO> genders = rhservice.getAllHr(session).getGenders();
            model.addAttribute("employees", employlist);
            model.addAttribute("genders", genders);
            return "employees";
        }
    }
    @GetMapping("/searchEmployees")
    public String searchEmployees(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Model model,
            HttpSession session,RestTemplate restTemplate) {
        
        String token = (String) session.getAttribute("accessToken");

        if (token == null) {
            return "redirect:/api/auth/";
        }
        // Récupérer la liste des employés et genres
        HrService rhservice = new HrService(restTemplate);
        List<EmployeeDTO> employlist = rhservice.getAllHr(session).getEmployees();
        List<GenderDTO> genders = rhservice.getAllHr(session).getGenders();


        // Convertir les dates en LocalDate
        LocalDate start = null;
        LocalDate end = null;
        try {
            if (startDate != null && !startDate.trim().isEmpty()) {
                start = LocalDate.parse(startDate);
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                end = LocalDate.parse(endDate);
            }
        } catch (DateTimeParseException e) {
            model.addAttribute("error", "Format de date invalide. Utilisez AAAA-MM-JJ.");
        }

        // Filtrer les employés avec searchByCriteria
        if (search != null || gender != null || start != null || end != null) {
            final String finalSearch = search;
            final String finalGender = gender;
            final LocalDate finalStart = start;
            final LocalDate finalEnd = end;
            employlist = employlist.stream()
                    .filter(employee -> employee.searchByCriteria(finalSearch, finalGender, finalStart, finalEnd))
                    .collect(Collectors.toList());
        }

        model.addAttribute("employees", employlist);
        model.addAttribute("genders", genders);
        model.addAttribute("search", search);
        model.addAttribute("gender", gender);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "employees";
    }
    @GetMapping("/fiche")
    public String showfichePage(@RequestParam("employee") String employee,Model model,HttpSession session,RestTemplate restTemplate) {
        String token = (String) session.getAttribute("accessToken");

        if (token == null) {
            return "redirect:/api/auth/";
        }
        else{    
            HrService rhservice = new HrService(restTemplate);
            List<EmployeeDTO> employlist = rhservice.getAllHr(session).getEmployees();
            List<SalarySlipDTO> salaryslip = rhservice.getAllHr(session).getSalarys();
            List<SalaryDetailDTO> salarydetails = rhservice.getAllHr(session).getSalarydetail();
            EmployeeDTO emplyeeconcerned=EmployeeDTO.getbyID(employlist, employee);
            emplyeeconcerned.setSalarySlip(salaryslip);
            for(int i=0;i<emplyeeconcerned.getSalarySlip().size();i++){
                emplyeeconcerned.getSalarySlip().get(i).setCorrespond(salarydetails);
            }
            model.addAttribute("employee", emplyeeconcerned);
            return "employee_details";
        }
    }
    
    @GetMapping("/salarySlip")
    public String showSalarySlipPage(
            @RequestParam("employeeId") String employeeId,
            @RequestParam("slipName") String slipName,
            Model model,
            HttpSession session,RestTemplate restTemplate) {
        String token = (String) session.getAttribute("accessToken");
        if (token == null) {
            System.err.println("No access token in session for salarySlip: " + employeeId + "/" + slipName);
            return "redirect:/api/auth/";
        }

        try {
            HrService hrService = new HrService(restTemplate);
            AllHRDataDTO hrData = hrService.getAllHr(session);
            List<EmployeeDTO> employlist = hrService.getAllHr(session).getEmployees();
            List<SalarySlipDTO> salaryslip = hrService.getAllHr(session).getSalarys();
            List<SalaryDetailDTO> salarydetails = hrService.getAllHr(session).getSalarydetail();
            if (hrData == null || hrData.getEmployees() == null) {
                System.err.println("No HR data or employees found for employeeId: " + employeeId);
                model.addAttribute("error", "Aucune donnée disponible.");
                return "salary_slip";
            }
            EmployeeDTO emplyeeconcerned=EmployeeDTO.getbyID(employlist, employeeId);
            emplyeeconcerned.setSalarySlip(salaryslip);
            for(int i=0;i<emplyeeconcerned.getSalarySlip().size();i++){
                emplyeeconcerned.getSalarySlip().get(i).setCorrespond(salarydetails);
            }
            SalarySlipDTO salarySlip=SalarySlipDTO.getbyID(emplyeeconcerned.getSalarySlip(), slipName);
            // Convert netPay to words
            String netPayInWords = numberToWords(salarySlip.getNetPay() != null ? salarySlip.getNetPay().longValue() : 0);

            model.addAttribute("employee", emplyeeconcerned);
            model.addAttribute("salarySlip", salarySlip);
            model.addAttribute("netPayInWords", netPayInWords);
            return "salary_slip";
        } catch (Exception e) {
            System.err.println("Error fetching salary slip for employeeId: " + employeeId + ", slipName: " + slipName + ": " + e.getMessage());
            model.addAttribute("error", "Erreur lors de la récupération de la fiche de paie : " + e.getMessage());
            return "salary_slip";
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
                if (group >= 10) {
                    groupWords += teens[group - 10] + " ";
                    group = 0;
                }
                if (group > 0 && group < 10) {
                    groupWords += units[group] + " ";
                words.insert(0, groupWords);
                }
                words.insert(0, groupWords + thousands[groupIndex] + "");
            }
            number /= 1000;
            groupIndex++;
        }

        return words.toString().trim();
    }
   @GetMapping("/pdf")
    public ResponseEntity<byte[]> downloadPayslipPdf(
            @RequestParam("employeeId") String employeeId,
            @RequestParam("slipName") String slipName,
            HttpSession session,RestTemplate restTemplate) {
        String token = (String) session.getAttribute("accessToken");
        if (token == null) {
            System.err.println("Token absent in session for employeeId: " + employeeId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        try {
            System.out.println("Processing PDF download for employeeId: " + employeeId + ", slipName: " + slipName);
            HrService hrService = new HrService(restTemplate);
            AllHRDataDTO hrData = hrService.getAllHr(session);
            List<EmployeeDTO> listemployee=hrData.getEmployees();
            if (hrData == null || listemployee == null) {
                System.err.println("No HR data or employees found for employeeId: " + employeeId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            EmployeeDTO employee = EmployeeDTO.getbyID(listemployee, employeeId);
            if (employee == null) {
                System.err.println("Employee not found: " + employeeId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            List<SalarySlipDTO> salaryslip = hrData.getSalarys();
            List<SalaryDetailDTO> salarydetails = hrData.getSalarydetail();
            employee.setSalarySlip(salaryslip);
            for(int i=0;i<employee.getSalarySlip().size();i++){
                employee.getSalarySlip().get(i).setCorrespond(salarydetails);
            }

            SalarySlipDTO salarySlip = employee.getSalarySlip().stream()
                    .filter(slip -> slip.getName() != null && slip.getName().equals(slipName))
                    .findFirst()
                    .orElse(null);
            if (salarySlip == null) {
                System.err.println("Salary Slip not found: " + slipName + " for employeeId: " + employeeId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            
            byte[] pdfContent = hrService.generatePayslipPdf(employee, salarySlip);
            String safeFilename = "salary-slip-" + slipName.replaceAll("[^a-zA-Z0-9.-]", "_") + ".pdf";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + safeFilename + "\"")
                    .body(pdfContent);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error for employeeId: " + employeeId + ", slipName: " + slipName + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            System.err.println("Server error for employeeId: " + employeeId + ", slipName: " + slipName + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping("/salaries")
    public String showSalariesPage(Model model,HttpSession session,RestTemplate restTemplate) {
        try {
        HrService rhservice = new HrService(restTemplate);
        List<EmployeeDTO> employlist = rhservice.getAllHr(session).getEmployees();
        List<SalarySlipDTO> salaryslip = rhservice.getAllHr(session).getSalarys();
        List<SalaryDetailDTO> salarydetails = rhservice.getAllHr(session).getSalarydetail();
         for(int i=0;i<employlist.size();i++){
            employlist.get(i).setSalarySlip(salaryslip);
            for(int a=0;a<employlist.get(i).getSalarySlip().size();a++){
                employlist.get(i).getSalarySlip().get(a).setCorrespond(salarydetails);
            }
        }
        Double totalnets=EmployeeDTO.getotalNetPay(employlist);
        Double totalgross=EmployeeDTO.getotalGrosspay(employlist);
        Double totaldeductions=EmployeeDTO.getotalDeduction(employlist);
        System.out.println(employlist.size());

         model.addAttribute("employees", employlist);
         model.addAttribute("totalnets", totalnets);
         model.addAttribute("totalgross", totalgross);
         model.addAttribute("totaldeductions", totaldeductions);
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des données : " + e.getMessage());
        }

        return "salaries";
    }
    @GetMapping("/searchmounth")
    public String showSalarieSearch(
            @RequestParam(required = false) String month,Model model,HttpSession session,RestTemplate restTemplate) {
        try {
            List<EmployeeDTO> filteredEmployees=EmployeeDTO.filterEmployeesByMonth(month, restTemplate, session);    
            Double totalnets=EmployeeDTO.getotalNetPay(filteredEmployees);
            Double totalgross=EmployeeDTO.getotalGrosspay(filteredEmployees);
            Double totaldeductions=EmployeeDTO.getotalDeduction(filteredEmployees);
            model.addAttribute("employees", filteredEmployees);
            model.addAttribute("month", month);
            model.addAttribute("totalnets", totalnets);
            model.addAttribute("totalgross", totalgross);
            model.addAttribute("totaldeductions", totaldeductions);
        } catch (Exception e) {
            model.addAttribute("error", "Erreur lors de la récupération des données : " + e.getMessage());
        }

        return "salaries";
    }
    @GetMapping("/statistic")
    public String showStatPage(Model model,HttpSession session,RestTemplate restTemplate) {
        try {
        HrService rhservice = new HrService(restTemplate);
        List<StatisticDTO> statistics = rhservice.getAllHr(session).getStatistics();
        Set<String> availableYears = new HashSet<>();
        if (statistics != null) {
            for (StatisticDTO stat : statistics) {
                if (stat != null && stat.getMonth() != null) {
                    availableYears.add(stat.getMonth().substring(0, 4));
                }
            }
        }
        // Préparer les données pour le graphique
            List<String> months = statistics.stream()
                    .map(StatisticDTO::getMonth)
                    .collect(Collectors.toList());
            List<Double> grossPay = statistics.stream()
                    .map(stat -> stat.getTotalGrossPay() != null ? stat.getTotalGrossPay() : 0.0)
                    .collect(Collectors.toList());
            List<Double> deductions = statistics.stream()
                    .map(stat -> stat.getTotalDeductions() != null ? stat.getTotalDeductions() : 0.0)
                    .collect(Collectors.toList());
            List<Double> netPay = statistics.stream()
                    .map(stat -> stat.getTotalNetPay() != null ? stat.getTotalNetPay() : 0.0)
                    .collect(Collectors.toList());

            ChartData chartData = new ChartData(months, grossPay, deductions, netPay);

         model.addAttribute("monthlyStats", statistics);
         model.addAttribute("availableYears", availableYears);
         model.addAttribute("chartData", chartData);
        } catch (Exception e) {
        model.addAttribute("error", "Erreur lors de la récupération des données : " + e.getMessage());
        }

        return "statistics";
    }
    @GetMapping("/searchstat")
    public String getStatistics(Model model, @RequestParam(value = "year", required = false) String year,HttpSession session
    ,RestTemplate restTemplate) {
        
        HrService rhservice = new HrService(restTemplate);
        List<StatisticDTO> allStats = rhservice.getAllHr(session).getStatistics(); 
        // Filtrer les statistiques par année en utilisant StatisticDTO.getByYear
        List<StatisticDTO> monthlyStats = year != null && !year.isEmpty()
                ? StatisticDTO.getByYear(allStats, year)
                : allStats;

        // Calculer les années disponibles
        Set<String> availableYears = new HashSet<>();
        if (allStats != null) {
            for (StatisticDTO stat : allStats) {
                if (stat != null && stat.getMonth() != null) {
                    availableYears.add(stat.getMonth().substring(0, 4));
                }
            }
        }
        // Préparer les données pour le graphique
            List<String> months = monthlyStats.stream()
                    .map(StatisticDTO::getMonth)
                    .collect(Collectors.toList());
            List<Double> grossPay = monthlyStats.stream()
                    .map(stat -> stat.getTotalGrossPay() != null ? stat.getTotalGrossPay() : 0.0)
                    .collect(Collectors.toList());
            List<Double> deductions = monthlyStats.stream()
                    .map(stat -> stat.getTotalDeductions() != null ? stat.getTotalDeductions() : 0.0)
                    .collect(Collectors.toList());
            List<Double> netPay = monthlyStats.stream()
                    .map(stat -> stat.getTotalNetPay() != null ? stat.getTotalNetPay() : 0.0)
                    .collect(Collectors.toList());

            ChartData chartData = new ChartData(months, grossPay, deductions, netPay);
        // Ajouter les attributs au modèle
        model.addAttribute("monthlyStats", monthlyStats);
        model.addAttribute("availableYears", availableYears);
        model.addAttribute("selectedYear", year);
        model.addAttribute("chartData", chartData);
        return "statistics";
    }
    @GetMapping("/statdetail")
    public String showstatdetailPage(@RequestParam("month") String month,Model model,HttpSession session,RestTemplate restTemplate) {
        String token = (String) session.getAttribute("accessToken");

        if (token == null) {
            return "redirect:/api/auth/";
        }
        else{    
            HrService rhservice = new HrService(restTemplate);
            List<EmployeeDTO> employlist = rhservice.getAllHr(session).getEmployees();
            List<SalarySlipDTO> salaryslip = rhservice.getAllHr(session).getSalarys();
            List<SalaryDetailDTO> salarydetails = rhservice.getAllHr(session).getSalarydetail();
            List<StatisticDTO> statistics = rhservice.getAllHr(session).getStatistics();   
            for(int i=0;i<employlist.size();i++){
            employlist.get(i).setSalarySlip(salaryslip);
                for(int a=0;a<employlist.get(i).getSalarySlip().size();a++){
                employlist.get(i).getSalarySlip().get(a).setCorrespond(salarydetails);
                }
            }
            StatisticDTO stat=StatisticDTO.getbymonth(statistics, month);
            List<EmployeeDTO> emplyeeconcerned=EmployeeDTO.getByPeriod(employlist, month);
            model.addAttribute("concernedEmployees", emplyeeconcerned);
            model.addAttribute("stat",stat);
            return "stat_details";
        }
    }
}
