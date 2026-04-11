package com.app.registroponto.repository;

import com.app.registroponto.model.TimeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeRecordRepository extends JpaRepository<TimeRecord, Long> {
    Optional<TimeRecord> findFirstByEmployeeIdAndClockOutTimeIsNullOrderByClockInTimeDesc(Long employeeId);
    List<TimeRecord> findByEmployeeIdOrderByClockInTimeDesc(Long employeeId);
}
