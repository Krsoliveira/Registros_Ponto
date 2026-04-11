package com.app.registro_ponto.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String employeeNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String shift; // e.g. "A", "B", "C", "Comercial"

    @Column(nullable = false)
    private Integer weeklyWorkload; // e.g. 44

    public Employee() {}

    public Employee(String employeeNumber, String name, String shift, Integer weeklyWorkload) {
        this.employeeNumber = employeeNumber;
        this.name = name;
        this.shift = shift != null ? shift : "Comercial";
        this.weeklyWorkload = weeklyWorkload != null ? weeklyWorkload : 44;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }

    public Integer getWeeklyWorkload() { return weeklyWorkload; }
    public void setWeeklyWorkload(Integer weeklyWorkload) { this.weeklyWorkload = weeklyWorkload; }
}
