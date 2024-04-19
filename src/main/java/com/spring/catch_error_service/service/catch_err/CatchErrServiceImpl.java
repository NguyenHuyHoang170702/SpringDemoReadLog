package com.spring.catch_error_service.service.catch_err;


import com.spring.catch_error_service.service.file.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CatchErrServiceImpl implements CatchErrService {

    @Autowired
    private FileService fileService;

    @Override
    public List<String> getLogByErrorId() {
        List<String> lstRequestString = new ArrayList<>();
        List<String> lstErrorRequestStrings = getAllErrorRequest();
        try {
            lstErrorRequestStrings.forEach(item -> {
                String[] LogArr = item.split(" ");
                String request = LogArr[4] + "///" + LogArr[7];
                lstRequestString.add(request);
            });
            return lstRequestString;
        } catch (Exception e) {
            log.error("error to get log by error id: " + e.getMessage());
        }
        return null;
    }

    public List<String> findErrorId() {
        try {
            List<String> allDataList = fileService.allFilesData();
            List<String> errorIdArrList = new ArrayList<>();
            for (String dataLine : allDataList) {
                if (dataLine.contains("[ERROR]")) {
                    String[] errorLine = dataLine.split(" ");
                    String errorID = errorLine[4];
                    errorIdArrList.add(errorID);
                }
            }
            if (errorIdArrList.isEmpty()) {
                log.info("don't have any response error");
            }
            return errorIdArrList;
        } catch (Exception e) {
            log.error("error to find error request id: " + e.getMessage());
        }
        return null;
    }

    public List<String> getAllErrorRequest() {
        List<String> listErrorId = findErrorId();
        List<String> allDataList = fileService.allFilesData();
        try {
            return allDataList.stream()
                    .filter(oneLine ->
                                    !oneLine.contains("[ERROR]") &&
                                    oneLine.contains("[sendIPNReceipt]") &&
                                    oneLine.contains("Request")
                    ).filter(oneLine -> listErrorId.stream().anyMatch(oneLine::contains))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("error to get log string: " + e.getMessage());
        }
        return null;
    }

}
