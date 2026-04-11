package com.app.registroponto.controller;

import com.app.registroponto.model.Employee;
import com.app.registroponto.model.TimeRecord;
import com.app.registroponto.repository.EmployeeRepository;
import com.app.registroponto.repository.TimeRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TimeTrackingController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TimeRecordRepository timeRecordRepository;

    @PostMapping("/employees")
    public ResponseEntity<?> registerEmployee(@RequestBody Employee employee) {
        if (employeeRepository.findByEmployeeNumber(employee.getEmployeeNumber()).isPresent()) {
            return ResponseEntity.badRequest().body("Matricula já cadastrada.");
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
            // Clock out
            TimeRecord record = openRecord.get();
            record.setClockOutTime(LocalDateTime.now());
            timeRecordRepository.save(record);
            return ResponseEntity.ok("Saída registrada para " + employee.getName() + " às " + record.getClockOutTime());
        } else {
            // Clock in
            TimeRecord record = new TimeRecord(employee, LocalDateTime.now());
            timeRecordRepository.save(record);
            return ResponseEntity.ok("Entrada registrada para " + employee.getName() + " às " + record.getClockInTime());
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
}
