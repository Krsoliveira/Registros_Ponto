package com.app.registro_ponto.repository;

import com.app.registro_ponto.model.TimeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeRecordRepository extends JpaRepository<TimeRecord, Long> {
    Optional<TimeRecord> findFirstByEmployeeIdAndClockOutTimeIsNullOrderByClockInTimeDesc(Long employeeId);
    List<TimeRecord> findByEmployeeIdOrderByClockInTimeDesc(Long employeeId);
    List<TimeRecord> findByEmployeeIdAndClockInTimeBetweenOrderByClockInTimeDesc(Long employeeId, LocalDateTime start, LocalDateTime end);
}
