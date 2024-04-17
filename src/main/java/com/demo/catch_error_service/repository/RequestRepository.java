package com.demo.catch_error_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.demo.catch_error_service.entity.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer>{
	
}
