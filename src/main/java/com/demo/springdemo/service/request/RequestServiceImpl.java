package com.demo.springdemo.service.request;


import com.demo.springdemo.entity.Request;
import com.demo.springdemo.repository.RequestRepository;
import com.demo.springdemo.service.catchErr.CatchErrService;
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
public class RequestServiceImpl implements RequestService{
    @Autowired
    private CatchErrService catchErrService;
    @Autowired
    private RequestRepository requestRepository;
    @Override
    public void saveAllRequest() {
        try {
            List<String> data = catchErrService.getLogByErrorId();
            ObjectMapper objectMapper = new ObjectMapper();
            List<Request> requestList = new ArrayList<>();
            data.forEach(item -> {
                Map<String, Object> requestObject;
                String[] LogArr = item.split("///");
                try {
                    requestObject = objectMapper.readValue(LogArr[1], Map.class);

                    String url = (String) requestObject.get("url");
                    String method = (String) requestObject.get("method");
                    String json = objectMapper.writeValueAsString(requestObject.get("json"));

                    Request request = new Request();
                    request.setUrl(url);
                    request.setRequestId(LogArr[0]);
                    request.setMethod(method);
                    request.setJsonData(json);
                    request.setCreateDate(new Date());
                    request.setUpdateDate(new Date());
                    request.setRetry(true);
                    request.setPush(true);
                    requestList.add(request);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            });
            requestRepository.saveAll(requestList);
        }catch (Exception e){
            log.error("error to save request: "+e.getMessage());
        }
    }
}
