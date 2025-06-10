package com.example.demo.controller;
import com.example.demo.dto.*;
import com.example.demo.model.*;
import com.example.demo.service.*;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
@Controller
@RequestMapping("/api/Hr")
public class HRController {
@GetMapping("/employeeList")
public String showEmployeePage(Model model, 
                               HttpSession session, 
                               RestTemplate restTemplate,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
    String token = (String) session.getAttribute("accessToken");
    if (token == null) {
        return "redirect:/api/auth/";
    }

    try {
        HrService rhservice = new HrService(restTemplate);
        List<EmployeeDTO> allEmployees = rhservice.getAllHr(session).getEmployees();
        List<GenderDTO> genders = rhservice.getAllHr(session).getGenders();

        // Gestion de la pagination
        int totalRecords = allEmployees != null ? allEmployees.size() : 0;
        int totalPages = (int) Math.ceil((double) totalRecords / size);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalRecords);

        if (startIndex >= totalRecords && totalRecords > 0) {
            page = totalPages - 1;
            startIndex = page * size;
            endIndex = Math.min(startIndex + size, totalRecords);
        } else if (totalRecords == 0) {
            startIndex = 0;
            endIndex = 0;
        }

        List<EmployeeDTO> employlist = allEmployees != null && !allEmployees.isEmpty()
            ? allEmployees.subList(startIndex, endIndex)
            : new ArrayList<>();

        model.addAttribute("employees", employlist);
        model.addAttribute("genders", genders);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalRecords", totalRecords);
        model.addAttribute("isSearch", false); // Indique que ce n'est pas une recherche
        model.addAttribute("search", null);
        model.addAttribute("gender", null);
        model.addAttribute("startDate", null);
        model.addAttribute("endDate", null);

    } catch (Exception e) {
        System.err.println("Erreur dans showEmployeePage: " + e.getMessage());
        model.addAttribute("error", "Erreur lors de la récupération des données : " + e.getMessage());
    }

    return "employees";
}
    @GetMapping("/searchEmployees")
public String searchEmployees(
        @RequestParam(required = false) String search,
        @RequestParam(required = false) String gender,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size, 
        Model model,
        HttpSession session,
        RestTemplate restTemplate) {
    
    String token = (String) session.getAttribute("accessToken");
    if (token == null) {
        return "redirect:/api/auth/";
    }

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

    try {
        HrService rhservice = new HrService(restTemplate);
        List<EmployeeDTO> employlist = rhservice.getAllHr(session).getEmployees();
        List<GenderDTO> genders = rhservice.getAllHr(session).getGenders();

        // Filtrer les employés
        if (search != null || gender != null || start != null || end != null) {
            final String finalSearch = search;
            final String finalGender = gender;
            final LocalDate finalStart = start;
            final LocalDate finalEnd = end;
            employlist = employlist.stream()
                    .filter(employee -> employee.searchByCriteria(finalSearch, finalGender, finalStart, finalEnd))
                    .collect(Collectors.toList());
        }

        // Gestion de la pagination
        int totalRecords = employlist != null ? employlist.size() : 0;
        int totalPages = (int) Math.ceil((double) totalRecords / size);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalRecords);

        if (startIndex >= totalRecords && totalRecords > 0) {
            page = totalPages - 1;
            startIndex = page * size;
            endIndex = Math.min(startIndex + size, totalRecords);
        } else if (totalRecords == 0) {
            startIndex = 0;
            endIndex = 0;
        }

        List<EmployeeDTO> emp = employlist != null && !employlist.isEmpty()
            ? employlist.subList(startIndex, endIndex)
            : new ArrayList<>();

        model.addAttribute("employees", emp);
        model.addAttribute("genders", genders);
        model.addAttribute("search", search);
        model.addAttribute("gender", gender);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalRecords", totalRecords);
        model.addAttribute("isSearch", true); 

    } catch (Exception e) {
        model.addAttribute("error", "Erreur lors de la récupération des données : " + e.getMessage());
    }

    return "employees";
}
    @GetMapping("/fiche")
    public String showfichePage(@RequestParam("employee") String employee,Model model,HttpSession session,RestTemplate restTemplate,
        @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "5") int size) {
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
            // Gestion de la pagination
            int totalRecords = emplyeeconcerned.getSalarySlip() != null ? emplyeeconcerned.getSalarySlip().size() : 0;
            int totalPages = (int) Math.ceil((double) totalRecords / size);
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, totalRecords);

            if (startIndex >= totalRecords && totalRecords > 0) {
                page = totalPages - 1;
                startIndex = page * size;
                endIndex = Math.min(startIndex + size, totalRecords);
            } else if (totalRecords == 0) {
                startIndex = 0;
                endIndex = 0;
            }

            List<SalarySlipDTO> slipconrened = emplyeeconcerned.getSalarySlip() != null && !emplyeeconcerned.getSalarySlip().isEmpty()
                ? emplyeeconcerned.getSalarySlip().subList(startIndex, endIndex)
                : new ArrayList<>();
            model.addAttribute("employee", emplyeeconcerned);
            model.addAttribute("slip", slipconrened);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalRecords", totalRecords);
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
    public String showSalariesPage(Model model,
                                  HttpSession session,
                                  RestTemplate restTemplate,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size) {
        try {
            String token = (String) session.getAttribute("accessToken");
            if (token == null) {
                return "redirect:/api/auth/";
            }
            HrService rhservice = new HrService(restTemplate);
            List<EmployeeDTO> employlist = rhservice.getAllHr(session).getEmployees();
            List<SalarySlipDTO> salaryslip = rhservice.getAllHr(session).getSalarys();
            List<SalaryDetailDTO> salarydetails = rhservice.getAllHr(session).getSalarydetail();

            // Associer les fiches de salaire et les détails aux employés
            for (int i = 0; i < employlist.size(); i++) {
                employlist.get(i).setSalarySlip(salaryslip);
                for (int a = 0; a < employlist.get(i).getSalarySlip().size(); a++) {
                    employlist.get(i).getSalarySlip().get(a).setCorrespond(salarydetails);
                }
            }

            // Calculer les totaux
            Double totalnets = EmployeeDTO.getotalNetPay(employlist);
            Double totalgross = EmployeeDTO.getotalGrosspay(employlist);
            Double totaldeductions = EmployeeDTO.getotalDeduction(employlist);
            System.out.println("Employlist size: " + employlist.size());

            // Gestion de la pagination
            if (page < 0) {
                page = 0;
            }
            int totalRecords = employlist != null ? employlist.size() : 0;
            int totalPages = (int) Math.ceil((double) totalRecords / size);
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, totalRecords);

            if (startIndex >= totalRecords && totalRecords > 0) {
                page = totalPages - 1;
                startIndex = page * size;
                endIndex = Math.min(startIndex + size, totalRecords);
            } else if (totalRecords == 0) {
                startIndex = 0;
                endIndex = 0;
            }

            // Paginer la liste des employés
            List<EmployeeDTO> paginatedEmployees = employlist != null && !employlist.isEmpty()
                    ? employlist.subList(startIndex, endIndex)
                    : new ArrayList<>();

            // Ajouter les attributs au modèle
            model.addAttribute("employees", paginatedEmployees);
            model.addAttribute("totalnets", totalnets);
            model.addAttribute("totalgross", totalgross);
            model.addAttribute("totaldeductions", totaldeductions);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalRecords", totalRecords);

        } catch (Exception e) {
            System.err.println("Erreur dans showSalariesPage: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors de la récupération des données : " + e.getMessage());
        }

        return "salaries";
    }

    @GetMapping("/searchmounth")
    public String showSalarieSearch(@RequestParam(required = false) String month,
                                   Model model,
                                   HttpSession session,
                                   RestTemplate restTemplate,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "5") int size) {
        try {
            String token = (String) session.getAttribute("accessToken");
            if (token == null) {
                return "redirect:/api/auth/";
            }
            List<EmployeeDTO> filteredEmployees = EmployeeDTO.filterEmployeesByMonth(month, restTemplate, session);    
            
            // Calculer les totaux
            Double totalnets = EmployeeDTO.getotalNetPay(filteredEmployees);
            Double totalgross = EmployeeDTO.getotalGrosspay(filteredEmployees);
            Double totaldeductions = EmployeeDTO.getotalDeduction(filteredEmployees);
            System.out.println("FilteredEmployees size: " + filteredEmployees.size());

            // Gestion de la pagination
            if (page < 0) {
                page = 0;
            }
            int totalRecords = filteredEmployees != null ? filteredEmployees.size() : 0;
            int totalPages = (int) Math.ceil((double) totalRecords / size);
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, totalRecords);

            if (startIndex >= totalRecords && totalRecords > 0) {
                page = totalPages - 1;
                startIndex = page * size;
                endIndex = Math.min(startIndex + size, totalRecords);
            } else if (totalRecords == 0) {
                startIndex = 0;
                endIndex = 0;
            }

            // Paginer la liste des employés filtrés
            List<EmployeeDTO> paginatedEmployees = filteredEmployees != null && !filteredEmployees.isEmpty()
                    ? filteredEmployees.subList(startIndex, endIndex)
                    : new ArrayList<>();

            // Ajouter les attributs au modèle
            model.addAttribute("employees", paginatedEmployees);
            model.addAttribute("month", month);
            model.addAttribute("totalnets", totalnets);
            model.addAttribute("totalgross", totalgross);
            model.addAttribute("totaldeductions", totaldeductions);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalRecords", totalRecords);

        } catch (Exception e) {
            System.err.println("Erreur dans showSalarieSearch: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors de la récupération des données : " + e.getMessage());
        }

        return "salaries";
    }
   @GetMapping("/statistic")
    public String showStatPage(Model model, HttpSession session, RestTemplate restTemplate,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size) {
        try {
            String token = (String) session.getAttribute("accessToken");
            if (token == null) {
                return "redirect:/api/auth/";
            }
            HrService rhservice = new HrService(restTemplate);
            List<StatisticDTO> statistics = rhservice.getAllHr(session).getStatistics();
            List<EmployeeDTO> employelist = rhservice.getAllHr(session).getEmployees();

            // Log des tailles pour débogage
            System.out.println("Statistiques size: " + (statistics != null ? statistics.size() : 0));
            System.out.println("Employés size: " + (employelist != null ? employelist.size() : 0));

            // Obtenir les années disponibles
            Set<String> availableYears = new HashSet<>();
            if (statistics != null) {
                for (StatisticDTO stat : statistics) {
                    if (stat != null && stat.getMonth() != null) {
                        availableYears.add(stat.getMonth().substring(0, 4));
                    }
                }
            }

            // Générer tous les mois de 2025 pour le graphique
            List<String> allMonths2025 = IntStream.rangeClosed(1, 12)
                    .mapToObj(month -> String.format("2025-%02d", month))
                    .collect(Collectors.toList());

            // Créer une map pour les données de statistics par mois
            Map<String, StatisticDTO> statsByMonth = statistics != null
                    ? statistics.stream()
                        .filter(stat -> stat.getMonth() != null)
                        .collect(Collectors.toMap(StatisticDTO::getMonth, stat -> stat))
                    : new HashMap<>();

            // Préparer les données pour le graphique (tous les mois de 2025)
            List<String> months = new ArrayList<>(allMonths2025);
            List<Double> grossPay = new ArrayList<>();
            List<Double> deductions = new ArrayList<>();
            List<Double> netPay = new ArrayList<>();

            for (String month : allMonths2025) {
                StatisticDTO stat = statsByMonth.get(month);
                grossPay.add(stat != null && stat.getTotalGrossPay() != null ? stat.getTotalGrossPay() : 0.0);
                deductions.add(stat != null && stat.getTotalDeductions() != null ? stat.getTotalDeductions() : 0.0);
                netPay.add(stat != null && stat.getTotalNetPay() != null ? stat.getTotalNetPay() : 0.0);
            }

            // Récupérer les ComponentStatDTO
            List<ComponentStatDTO> componentStats = rhservice.getAllHr(session).getComponentstats();
            System.out.println("Taille componentStats: " + (componentStats != null ? componentStats.size() : 0));
            if (componentStats != null) {
                for (int i = 0; i < componentStats.size(); i++) {
                    System.out.println("List: " + componentStats.get(i).getComponentName() + ", Month: " + 
                                       componentStats.get(i).getMonth() + ", Total: " + componentStats.get(i).getTotal());
                }
            } else {
                System.out.println("componentStats est null");
            }

            // Regrouper ComponentStat par mois et composant
            Map<String, Map<String, Double>> componentTotalsByMonth = new HashMap<>();
            if (componentStats != null) {
                for (ComponentStatDTO stat : componentStats) {
                    if (stat.getMonth() != null && stat.getComponentName() != null && stat.getTotal() != null) {
                        componentTotalsByMonth
                            .computeIfAbsent(stat.getMonth(), k -> new HashMap<>())
                            .merge(stat.getComponentName(), stat.getTotal(), Double::sum);
                    }
                }
            }

            // Préparer les données des composants pour le graphique
            Set<String> componentNames = componentTotalsByMonth.values().stream()
                    .flatMap(map -> map.keySet().stream())
                    .collect(Collectors.toSet());
            Map<String, List<Double>> componentData = new HashMap<>();
            for (String componentName : componentNames) {
                List<Double> monthlyTotals = new ArrayList<>();
                for (String month : allMonths2025) {
                    Double total = componentTotalsByMonth.getOrDefault(month, new HashMap<>())
                            .getOrDefault(componentName, 0.0);
                    monthlyTotals.add(total);
                }
                componentData.put(componentName, monthlyTotals);
            }
            System.out.println("ComponentNames: " + componentNames);
            System.out.println("ComponentData size: " + componentData.size());

            // Regrouper ComponentStat par mois pour la table
            Map<String, List<ComponentStatDTO>> componentStatsByMonth = componentStats != null
                    ? componentStats.stream()
                        .collect(Collectors.groupingBy(ComponentStatDTO::getMonth))
                    : new HashMap<>();
            System.out.println("ComponentStatsByMonth keys: " + componentStatsByMonth.keySet());

            // Gestion de la pagination
            if (page < 0) {
                page = 0;
            }
            int totalRecords = statistics != null ? statistics.size() : 0;
            int totalPages = (int) Math.ceil((double) totalRecords / size);
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, totalRecords);

            if (startIndex >= totalRecords && totalRecords > 0) {
                page = totalPages - 1;
                startIndex = page * size;
                endIndex = Math.min(startIndex + size, totalRecords);
            } else if (totalRecords == 0) {
                startIndex = 0;
                endIndex = 0;
            }

            List<StatisticDTO> paginatedStats = statistics != null && !statistics.isEmpty()
                    ? statistics.subList(startIndex, endIndex)
                    : new ArrayList<>();

            // Créer l'objet ChartData
            ChartData chartData = new ChartData(months, grossPay, deductions, netPay, componentData);

            model.addAttribute("monthlyStats", paginatedStats);
            model.addAttribute("componentStatsByMonth", componentStatsByMonth);
            model.addAttribute("availableYears", availableYears);
            model.addAttribute("chartData", chartData);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalRecords", totalRecords);

        } catch (Exception e) {
            System.err.println("Erreur dans showStatPage: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors de la récupération des données : " + e.getMessage());
        }

        return "statistics";
    }

    @GetMapping("/searchstat")
    public String getStatistics(Model model, 
                               @RequestParam(value = "year", required = false) String year,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size,
                               HttpSession session, 
                               RestTemplate restTemplate) {
        try {
            String token = (String) session.getAttribute("accessToken");
            if (token == null) {
                return "redirect:/api/auth/";
            }
            HrService rhservice = new HrService(restTemplate);
            List<StatisticDTO> allStats = rhservice.getAllHr(session).getStatistics();

            // Log pour débogage
            System.out.println("AllStats size: " + (allStats != null ? allStats.size() : 0));

            // Filtrer les statistiques par année
            List<StatisticDTO> monthlyStats = year != null && !year.isEmpty()
                    ? StatisticDTO.getByYear(allStats, year)
                    : allStats;
            System.out.println("MonthlyStats size: " + (monthlyStats != null ? monthlyStats.size() : 0));

            // Calculer les années disponibles
            Set<String> availableYears = new HashSet<>();
            if (allStats != null) {
                for (StatisticDTO stat : allStats) {
                    if (stat != null && stat.getMonth() != null) {
                        availableYears.add(stat.getMonth().substring(0, 4));
                    }
                }
            }

            // Gestion de la pagination
            if (page < 0) {
                page = 0;
            }
            int totalRecords = monthlyStats != null ? monthlyStats.size() : 0;
            int totalPages = (int) Math.ceil((double) totalRecords / size);
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, totalRecords);

            if (startIndex >= totalRecords && totalRecords > 0) {
                page = totalPages - 1;
                startIndex = page * size;
                endIndex = Math.min(startIndex + size, totalRecords);
            } else if (totalRecords == 0) {
                startIndex = 0;
                endIndex = 0;
            }

            List<StatisticDTO> paginatedStats = monthlyStats != null && !monthlyStats.isEmpty()
                    ? monthlyStats.subList(startIndex, endIndex)
                    : new ArrayList<>();

            // Déterminer l'année à utiliser pour le graphique
            String chartYear = year != null && !year.isEmpty() ? year : "2025";

            // Générer tous les mois pour l'année sélectionnée
            List<String> allMonths = IntStream.rangeClosed(1, 12)
                    .mapToObj(month -> String.format("%s-%02d", chartYear, month))
                    .collect(Collectors.toList());

            // Créer une map pour les données de monthlyStats par mois
            Map<String, StatisticDTO> statsByMonth = monthlyStats != null
                    ? monthlyStats.stream()
                        .filter(stat -> stat.getMonth() != null)
                        .collect(Collectors.toMap(StatisticDTO::getMonth, stat -> stat))
                    : new HashMap<>();

            // Préparer les données pour le graphique (tous les mois de l'année)
            List<String> months = new ArrayList<>(allMonths);
            List<Double> grossPay = new ArrayList<>();
            List<Double> deductions = new ArrayList<>();
            List<Double> netPay = new ArrayList<>();

            for (String month : allMonths) {
                StatisticDTO stat = statsByMonth.get(month);
                grossPay.add(stat != null && stat.getTotalGrossPay() != null ? stat.getTotalGrossPay() : 0.0);
                deductions.add(stat != null && stat.getTotalDeductions() != null ? stat.getTotalDeductions() : 0.0);
                netPay.add(stat != null && stat.getTotalNetPay() != null ? stat.getTotalNetPay() : 0.0);
            }

            // Récupérer les ComponentStatDTO
            List<ComponentStatDTO> componentStats = rhservice.getAllHr(session).getComponentstats();
            System.out.println("Taille componentStats: " + (componentStats != null ? componentStats.size() : 0));
            if (componentStats != null) {
                for (int i = 0; i < componentStats.size(); i++) {
                    System.out.println("List: " + componentStats.get(i).getComponentName() + ", Month: " + 
                                       componentStats.get(i).getMonth() + ", Total: " + componentStats.get(i).getTotal());
                }
            } else {
                System.out.println("componentStats est null");
            }

            // Filtrer ComponentStatDTO par année
            List<ComponentStatDTO> monthlycomponent = year != null && !year.isEmpty()
                    ? ComponentStatDTO.getByYear(componentStats, year)
                    : componentStats;
            System.out.println("MonthlyComponent size: " + (monthlycomponent != null ? monthlycomponent.size() : 0));

            // Regrouper ComponentStat par mois et composant pour le graphique
            Map<String, Map<String, Double>> componentTotalsByMonth = new HashMap<>();
            if (monthlycomponent != null) {
                for (ComponentStatDTO stat : monthlycomponent) {
                    if (stat.getMonth() != null && stat.getComponentName() != null && stat.getTotal() != null) {
                        componentTotalsByMonth
                            .computeIfAbsent(stat.getMonth(), k -> new HashMap<>())
                            .merge(stat.getComponentName(), stat.getTotal(), Double::sum);
                    }
                }
            }

            // Préparer les données des composants pour le graphique
            Set<String> componentNames = componentTotalsByMonth.values().stream()
                    .flatMap(map -> map.keySet().stream())
                    .collect(Collectors.toSet());
            Map<String, List<Double>> componentData = new HashMap<>();
            for (String componentName : componentNames) {
                List<Double> monthlyTotals = new ArrayList<>();
                for (String month : allMonths) {
                    Double total = componentTotalsByMonth.getOrDefault(month, new HashMap<>())
                            .getOrDefault(componentName, 0.0);
                    monthlyTotals.add(total);
                }
                componentData.put(componentName, monthlyTotals);
            }
            System.out.println("ComponentNames: " + componentNames);
            System.out.println("ComponentData size: " + componentData.size());

            // Regrouper ComponentStat par mois pour la table
            Map<String, List<ComponentStatDTO>> componentStatsByMonth = monthlycomponent != null
                    ? monthlycomponent.stream()
                        .collect(Collectors.groupingBy(ComponentStatDTO::getMonth))
                    : new HashMap<>();
            System.out.println("ComponentStatsByMonth keys: " + componentStatsByMonth.keySet());

            // Créer l'objet ChartData
            ChartData chartData = new ChartData(months, grossPay, deductions, netPay, componentData);

            // Ajouter les attributs au modèle
            model.addAttribute("monthlyStats", paginatedStats);
            model.addAttribute("availableYears", availableYears);
            model.addAttribute("componentStatsByMonth", componentStatsByMonth);
            model.addAttribute("selectedYear", year);
            model.addAttribute("chartData", chartData);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalRecords", totalRecords);

        } catch (Exception e) {
            System.err.println("Erreur dans getStatistics: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors de la récupération des données : " + e.getMessage());
        }

        return "statistics";
    }
    @GetMapping("/statdetail")
    public String showstatdetailPage(@RequestParam("month") String month,
                                    Model model,
                                    HttpSession session,
                                    RestTemplate restTemplate,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size) {
        String token = (String) session.getAttribute("accessToken");

        if (token == null) {
            return "redirect:/api/auth/";
        }

        try {
            HrService rhservice = new HrService(restTemplate);
            List<EmployeeDTO> employlist = rhservice.getAllHr(session).getEmployees();
            List<SalarySlipDTO> salaryslip = rhservice.getAllHr(session).getSalarys();
            List<SalaryDetailDTO> salarydetails = rhservice.getAllHr(session).getSalarydetail();
            List<StatisticDTO> statistics = rhservice.getAllHr(session).getStatistics();   

            // Associer les fiches de salaire et les détails aux employés
            for (int i = 0; i < employlist.size(); i++) {
                employlist.get(i).setSalarySlip(salaryslip);
                for (int a = 0; a < employlist.get(i).getSalarySlip().size(); a++) {
                    employlist.get(i).getSalarySlip().get(a).setCorrespond(salarydetails);
                }
            }

            // Récupérer les statistiques pour le mois
            StatisticDTO stat = StatisticDTO.getbymonth(statistics, month);
            // Filtrer les employés pour le mois spécifié
            List<EmployeeDTO> emplyeeconcerned = EmployeeDTO.getByPeriod(employlist, month);

            // Gestion de la pagination
            if (page < 0) {
                page = 0;
            }
            int totalRecords = emplyeeconcerned != null ? emplyeeconcerned.size() : 0;
            int totalPages = (int) Math.ceil((double) totalRecords / size);
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, totalRecords);

            if (startIndex >= totalRecords && totalRecords > 0) {
                page = totalPages - 1;
                startIndex = page * size;
                endIndex = Math.min(startIndex + size, totalRecords);
            } else if (totalRecords == 0) {
                startIndex = 0;
                endIndex = 0;
            }

            // Paginer la liste des employés concernés
            List<EmployeeDTO> paginatedEmployees = emplyeeconcerned != null && !emplyeeconcerned.isEmpty()
                    ? emplyeeconcerned.subList(startIndex, endIndex)
                    : new ArrayList<>();

            // Ajouter les attributs au modèle
            model.addAttribute("concernedEmployees", paginatedEmployees);
            model.addAttribute("stat", stat);
            model.addAttribute("month", month);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("totalRecords", totalRecords);

            return "stat_details";
        } catch (Exception e) {
            System.err.println("Erreur dans showstatdetailPage: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Erreur lors de la récupération des données : " + e.getMessage());
            return "stat_details";
        }
    }
}
