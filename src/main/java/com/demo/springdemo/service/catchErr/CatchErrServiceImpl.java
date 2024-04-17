package com.demo.springdemo.service.catchErr;


import com.demo.springdemo.service.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatchErrServiceImpl implements CatchErrService{

    @Autowired private FileService fileService;
    @Override
    public List<String> getLogByErrorId() {
        List<String> lstRequestString = new ArrayList<>();
        List<String> lstErrorRequestStrings = getLogStrings();
        try {
            lstErrorRequestStrings.forEach(item -> {
                String[] LogArr = item.split(" ");
                String request = LogArr[4]+"///"+LogArr[7];

                lstRequestString.add(request);
            });
            return lstRequestString;
        } catch (Exception e) {

        }
        return null;
    }

    public List<String> findErrorId() {
        List<String> allDataList = fileService.allFilesData();
        List<String> errorIdArrList = new ArrayList<>();
        for (String dataLine : allDataList) {
            if (dataLine.contains("[ERROR]")) {
                String[] errorLine = dataLine.split(" ");
                String errorID = errorLine[4];
                errorIdArrList.add(errorID);
            }
        }
        return errorIdArrList;
    }

    public List<String> getLogStrings() {
        List<String> lstErrorIdList = findErrorId();
        List<String> allDataList = fileService.allFilesData();
        try {
            List<String> lstDataList = allDataList.stream()
                    .filter(oneLine -> !oneLine.equals("[ERROR]")
                            && oneLine.contains("[sendIPNReceipt]") && oneLine.contains("Request"))
                    .filter(oneLine -> lstErrorIdList.stream().anyMatch(oneLine::contains))
                    .collect(Collectors.toList());
            return lstDataList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
