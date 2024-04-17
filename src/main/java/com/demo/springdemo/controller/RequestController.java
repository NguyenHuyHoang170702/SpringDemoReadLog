package com.demo.springdemo.controller;

import com.demo.springdemo.service.catchErr.CatchErrService;
import com.demo.springdemo.service.file.FileService;
import com.demo.springdemo.service.request.RequestService;
import com.demo.springdemo.service.server.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RequestController {

    @Autowired
    private RequestService requestService;

    //@Scheduled(cron = "*/10 * * * * *")
    @PostMapping("/read-log-file")
    public ResponseEntity<String> copy() {
		try {
            requestService.saveAllRequest();
			return ResponseEntity.status(HttpStatus.OK).body("save all data successfully");
		}catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error to save all data successfully");
		}
    }

}
