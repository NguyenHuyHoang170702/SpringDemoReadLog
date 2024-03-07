package com.demo.springdemo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.springdemo.entity.Request;
import com.demo.springdemo.repository.RequestRepository;

@Service
public class RequestService{
	@Autowired 
	private RequestRepository repository;
	
	public List<Request> saveAll(List<Request> requests) {
		return repository.saveAll(requests);
	}
	
}
