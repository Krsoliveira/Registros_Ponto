package com.app.registro_ponto.dto;

import com.app.registro_ponto.model.TimeRecord;
import java.util.List;

public class RecordSummary {
    private String employeeName;
    private String shift;
    private int weeklyWorkload;
    private double totalHoursWorked;
    private double overtime;
    private List<TimeRecord> records;

    public RecordSummary(String employeeName, String shift, int weeklyWorkload, double totalHoursWorked, double overtime, List<TimeRecord> records) {
        this.employeeName = employeeName;
        this.shift = shift;
        this.weeklyWorkload = weeklyWorkload;
        this.totalHoursWorked = totalHoursWorked;
        this.overtime = overtime;
        this.records = records;
    }

    public String getEmployeeName() { return employeeName; }
    public String getShift() { return shift; }
    public int getWeeklyWorkload() { return weeklyWorkload; }
    public double getTotalHoursWorked() { return totalHoursWorked; }
    public double getOvertime() { return overtime; }
    public List<TimeRecord> getRecords() { return records; }
}
