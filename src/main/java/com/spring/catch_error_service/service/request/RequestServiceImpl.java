package com.spring.catch_error_service.service.request;


import com.spring.catch_error_service.entity.ErrorFormat;
import com.spring.catch_error_service.entity.Request;
import com.spring.catch_error_service.repository.ErrorFormatRepository;
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
    @Autowired
    private ErrorFormatRepository errorFormatRepository;

    @Override
    public Boolean saveAllRequest() {
        try {
            List<String> data = catchErrService.getLogByErrorId();
            List<Request> requestList = new ArrayList<>();
            List<ErrorFormat> errorRequestFormatList = new ArrayList<>();

            for (String item : data) {
                processLogItem(item, requestList, errorRequestFormatList);
            }

            saveRequests(requestList);
            saveErrorFormats(errorRequestFormatList);
            return true;

        } catch (Exception e) {
            log.error("error to save request: " + e.getMessage());
        }
        return false;
    }

    private void processLogItem(String item, List<Request> requestList, List<ErrorFormat> errorRequestFormatList) {
        ObjectMapper objectMapper = new ObjectMapper();
        String[] logArr = item.split("///");

        try {
            String requestId = logArr.length >= 1 ? logArr[0] : "null";
            String requests = logArr.length >= 2 ? logArr[1] : "null";

            Map<String, Object> requestObject = objectMapper.readValue(requests, Map.class);

            if (!requestId.isEmpty() && !requests.isEmpty() &&
                    !requestId.equalsIgnoreCase("null") &&
                    !requests.equalsIgnoreCase("null")) {
                processValidRequest(requestObject, requestId, requestList, errorRequestFormatList);
            } else if (requests.isEmpty() || requests.equalsIgnoreCase("null")) {
                processEmptyRequest(requestId, errorRequestFormatList);
            }
        } catch (JsonProcessingException e) {
            log.error("error to parse json to object: " + e.getMessage());
        }
    }

    private void processValidRequest(Map<String, Object> requestObject, String requestId, List<Request> requestList, List<ErrorFormat> errorRequestFormatList) {
        String url = (String) requestObject.getOrDefault("url", "");
        String method = (String) requestObject.getOrDefault("method", "");
        String json = requestObject.containsKey("json") ? requestObject.get("json").toString() : "";

        if (!url.isEmpty() && !method.isEmpty() && !json.isEmpty()) {
            Request request = new Request();
            request.setRequestId(requestId);
            request.setUrl(url);
            request.setMethod(method);
            request.setJsonData(json);
            request.setCreateDate(new Date());
            request.setUpdateDate(new Date());
            request.setRetry(true);
            request.setPush(true);

            requestList.add(request);
        } else {
            ErrorFormat requestErrorFormat = new ErrorFormat();
            requestErrorFormat.setRequestId(requestId);
            requestErrorFormat.setUrl(url);
            requestErrorFormat.setMethod(method);
            requestErrorFormat.setJsonData(json);
            requestErrorFormat.setCreateDate(new Date());
            requestErrorFormat.setUpdateDate(new Date());
            requestErrorFormat.setRetry(true);
            requestErrorFormat.setPush(true);
            errorRequestFormatList.add(requestErrorFormat);
        }
    }

    private void processEmptyRequest(String requestId, List<ErrorFormat> errorRequestFormatList) {
        ErrorFormat requestErrorFormat = new ErrorFormat();
        requestErrorFormat.setRequestId(requestId);
        requestErrorFormat.setCreateDate(new Date());
        requestErrorFormat.setUpdateDate(new Date());
        requestErrorFormat.setRetry(true);
        requestErrorFormat.setPush(true);

        errorRequestFormatList.add(requestErrorFormat);
    }

    private void saveRequests(List<Request> requestList) {
        if (!requestList.isEmpty()) {
            requestRepository.saveAll(requestList);
        }
    }

    private void saveErrorFormats(List<ErrorFormat> errorRequestFormatList) {
        if (!errorRequestFormatList.isEmpty()) {
            errorFormatRepository.saveAll(errorRequestFormatList);
        }
    }
}
