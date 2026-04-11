package com.app.registro_ponto.controller;

import com.app.registro_ponto.dto.RecordSummary;
import com.app.registro_ponto.model.Employee;
import com.app.registro_ponto.model.TimeRecord;
import com.app.registro_ponto.repository.EmployeeRepository;
import com.app.registro_ponto.repository.TimeRecordRepository;
import com.app.registro_ponto.service.TimeTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TimeTrackingController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TimeRecordRepository timeRecordRepository;

    @Autowired
    private TimeTrackingService timeTrackingService;

    @PostMapping("/employees")
    public ResponseEntity<?> registerEmployee(@RequestBody Employee employee) {
        if (employeeRepository.findByEmployeeNumber(employee.getEmployeeNumber()).isPresent()) {
            return ResponseEntity.badRequest().body("Matrícula já cadastrada.");
        }
        if (employee.getWeeklyWorkload() == null) {
            employee.setWeeklyWorkload(44);
        }
        if (employee.getShift() == null || employee.getShift().isEmpty()) {
            employee.setShift("Comercial");
        }
        Employee saved = employeeRepository.save(employee);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @PostMapping("/records/clock")
    public ResponseEntity<?> clockTime(@RequestParam String employeeNumber) {
        Optional<Employee> employeeOpt = employeeRepository.findByEmployeeNumber(employeeNumber);
        if (employeeOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Funcionário não encontrado.");
        }
        
        Employee employee = employeeOpt.get();
        Optional<TimeRecord> openRecord = timeRecordRepository.findFirstByEmployeeIdAndClockOutTimeIsNullOrderByClockInTimeDesc(employee.getId());

        if (openRecord.isPresent()) {
            TimeRecord record = openRecord.get();
            record.setClockOutTime(LocalDateTime.now());
            timeRecordRepository.save(record);
            return ResponseEntity.ok("Saída registrada para " + employee.getName() + " às " + record.getClockOutTime().toLocalTime().withNano(0));
        } else {
            TimeRecord record = new TimeRecord(employee, LocalDateTime.now());
            timeRecordRepository.save(record);
            return ResponseEntity.ok("Entrada registrada para " + employee.getName() + " às " + record.getClockInTime().toLocalTime().withNano(0));
        }
    }

    @GetMapping("/records")
    public ResponseEntity<?> getRecords(@RequestParam String employeeNumber) {
        Optional<Employee> employeeOpt = employeeRepository.findByEmployeeNumber(employeeNumber);
        if (employeeOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Funcionário não encontrado.");
        }
        List<TimeRecord> records = timeRecordRepository.findByEmployeeIdOrderByClockInTimeDesc(employeeOpt.get().getId());
        return ResponseEntity.ok(records);
    }

    @GetMapping("/records/summary")
    public ResponseEntity<?> getSummary(@RequestParam String employeeNumber, @RequestParam String filter) {
        LocalDateTime start;
        LocalDateTime end = LocalDateTime.now();

        if ("week".equalsIgnoreCase(filter)) {
            start = end.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.MIN);
        } else if ("month".equalsIgnoreCase(filter)) {
            start = end.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
        } else {
            return ResponseEntity.badRequest().body("Filtro inválido. Use 'week' ou 'month'.");
        }

        try {
            RecordSummary summary = timeTrackingService.getSummary(employeeNumber, start, end);
            return ResponseEntity.ok(summary);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
