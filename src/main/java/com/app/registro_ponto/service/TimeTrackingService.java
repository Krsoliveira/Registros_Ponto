package com.app.registro_ponto.service;

import com.app.registro_ponto.dto.RecordSummary;
import com.app.registro_ponto.model.Employee;
import com.app.registro_ponto.model.TimeRecord;
import com.app.registro_ponto.repository.EmployeeRepository;
import com.app.registro_ponto.repository.TimeRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TimeTrackingService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TimeRecordRepository timeRecordRepository;

    public RecordSummary getSummary(String employeeNumber, LocalDateTime start, LocalDateTime end) {
        Optional<Employee> empOpt = employeeRepository.findByEmployeeNumber(employeeNumber);
        if (empOpt.isEmpty()) {
            throw new IllegalArgumentException("Funcionário não encontrado.");
        }
        
        Employee employee = empOpt.get();
        List<TimeRecord> records = timeRecordRepository.findByEmployeeIdAndClockInTimeBetweenOrderByClockInTimeDesc(employee.getId(), start, end);
        
        double totalHours = 0.0;
        for (TimeRecord record : records) {
            if (record.getClockOutTime() != null) {
                long minutes = Duration.between(record.getClockInTime(), record.getClockOutTime()).toMinutes();
                totalHours += (minutes / 60.0);
            }
        }
        
        totalHours = Math.round(totalHours * 100.0) / 100.0;
        
        // Calculate overtime based on weeks. 
        // We will approximate: 1 week = weeklyWorkload. 1 month = 4.33 * weeklyWorkload.
        double weeksInPeriod = Duration.between(start, end).toDays() / 7.0;
        if(weeksInPeriod < 1.0) weeksInPeriod = 1.0; 
        
        double expectedHours = employee.getWeeklyWorkload() * weeksInPeriod;
        double overtime = totalHours - expectedHours;
        if (overtime < 0) {
            overtime = 0.0; 
        } else {
            overtime = Math.round(overtime * 100.0) / 100.0;
        }

        return new RecordSummary(
                employee.getName(),
                employee.getShift(),
                employee.getWeeklyWorkload(),
                totalHours,
                overtime,
                records
        );
    }
}
