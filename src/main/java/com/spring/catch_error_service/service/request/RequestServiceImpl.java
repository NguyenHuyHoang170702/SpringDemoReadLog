package com.spring.catch_error_service.service.request;


import com.spring.catch_error_service.entity.Request;
import com.spring.catch_error_service.repository.RequestRepository;
import com.spring.catch_error_service.service.catch_err.CatchErrService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RequestServiceImpl implements RequestService {
    @Autowired
    private CatchErrService catchErrService;
    @Autowired
    private RequestRepository requestRepository;

    @Override
    public Boolean saveAllRequest() {
        try {
            List<String> data = catchErrService.getLogByErrorId();
            ObjectMapper objectMapper = new ObjectMapper();
            List<Request> requestList = new ArrayList<>();
            data.forEach(item -> {
                Map<String, Object> requestObject;
                String[] LogArr = item.split("///");
                try {
                    String requestId = LogArr[0];
                    String requests = LogArr[1];

                    requestObject = objectMapper.readValue(requests, Map.class);

                    String url = (String) requestObject.get("url");
                    String method = (String) requestObject.get("method");
                    String json = objectMapper.writeValueAsString(requestObject.get("json"));

                    Request request = new Request();
                    request.setUrl(url);
                    request.setRequestId(requestId);
                    request.setMethod(method);
                    request.setJsonData(json);
                    request.setCreateDate(new Date());
                    request.setUpdateDate(new Date());
                    request.setRetry(true);
                    request.setPush(true);

                    requestList.add(request);
                } catch (JsonProcessingException e) {
                    log.error("error to parse json to object: " + e.getMessage());
                }
            });
            requestRepository.saveAll(requestList);
            return true;

        } catch (Exception e) {
            log.error("error to save request: " + e.getMessage());
        }
        return false;
    }
}
