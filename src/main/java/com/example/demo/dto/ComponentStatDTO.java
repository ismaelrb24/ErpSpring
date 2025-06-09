package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ComponentStatDTO {
    @JsonProperty("month")
    private String month;

    @JsonProperty("salary_component")
    private String componentName;

    @JsonProperty("total_amount")
    private Double total;

    // Constructeur par défaut
    public ComponentStatDTO() {}
    // Getters et Setters
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
    public static List<ComponentStatDTO> getByYear(List<ComponentStatDTO> statistics, String year) {
        List<ComponentStatDTO> filteredStatistics = new ArrayList<>();
        if (statistics == null || year == null || year.isEmpty()) {
            return filteredStatistics;
        }

        for (ComponentStatDTO stat : statistics) {
            if (stat != null && stat.getMonth() != null && stat.getMonth().startsWith(year)) {
                filteredStatistics.add(stat);
            }
        }

        return filteredStatistics;
    }
    
}
