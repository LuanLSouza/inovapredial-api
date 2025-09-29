package com.inovapredial.repository;

import com.inovapredial.model.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CalendarRepository extends JpaRepository<Calendar, UUID> {
}
