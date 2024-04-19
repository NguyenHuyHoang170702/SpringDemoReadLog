package com.spring.catch_error_service.repository;

import com.spring.catch_error_service.entity.ErrorFormat;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorFormatRepository extends JpaRepository<ErrorFormat, Integer> {
}
