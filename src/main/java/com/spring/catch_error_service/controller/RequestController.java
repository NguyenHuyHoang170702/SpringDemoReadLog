package com.spring.catch_error_service.controller;

import com.spring.catch_error_service.service.request.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RequestController {

    @Autowired
    private RequestService requestService;

    //@Scheduled(cron = "0 5 0 * * *")
    @PostMapping("/push-request-err")
    public ResponseEntity<String> pushRequestDataToDB() {
        if (requestService.saveAllRequest()) {
            return ResponseEntity.status(HttpStatus.OK).body("save all data successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error to save all data");
        }
    }
}
