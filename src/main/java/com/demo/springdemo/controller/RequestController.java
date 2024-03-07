package com.demo.springdemo.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.demo.springdemo.entity.Request;
import com.demo.springdemo.service.RequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;
import lombok.experimental.var;

@Controller
public class RequestController {
    @Autowired
    private RequestService requestService;
    boolean isRetry = false;
    boolean isPush = false;
    
  //test @Scheduled(cron = "*/10 * * * * *")
    @Scheduled(cron = "0 5 * * * *")

    public void addAllRequest() {
    	String forderPath = "C:\\Users\\huyho\\Pc\\Desktop\\SampleData";
		List<String> allDataList = new ArrayList<>();
		try {
			Files.walk(Paths.get(forderPath)).filter(Files::isRegularFile).forEach(path -> {
				String fileName = path.getFileName().toString();
				if (fileName.endsWith(".log")) {
					// System.out.println("this is log file: "+ fileName);
					readLogFile(path, allDataList);

				} else if (fileName.endsWith(".zip") || fileName.endsWith(".rar")) {
					readLogZipFile(path.toFile(), allDataList);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
	
			List<Request> requests =  AddRequestToDB(allDataList);
			requestService.saveAll(requests);
			
		}
    }

    public void readLogFile(Path path, List<String> allDataList) {
		try (var oneLineData = Files.lines(path)) {
			oneLineData.forEach(item -> {
				allDataList.add(item);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readLogZipFile(File file, List<String> allDataList) {
		try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file))) {
			ZipEntry zipEntry;
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				if (zipEntry.getName().endsWith(".log")) {
					// System.out.println("cac file log trong file zip la: " + zipEntry.getName());
					// read log file inside zip
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipInputStream));
					String lineString;
					while ((lineString = bufferedReader.readLine()) != null) {
						// System.out.println(lineString);
						var oneLineData = lineString.split("\n");
						for (String data : oneLineData) {
							// System.out.println("data: "+ data);
							allDataList.add(data);
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<String> findErrorId(List<String> allDataList) {
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

	private List<String> getLogStrings(List<String> allDataList) {
		List<String> lstErrorIdList = findErrorId(allDataList);

		try {
			List<String> lstDataList = allDataList.stream()
					.filter(oneLine -> !oneLine.equals("[ERROR]")
					&& oneLine.contains("[sendIPNReceipt]") && oneLine.contains("Request"))
					.peek(oneLine -> {
						if (oneLine.contains("retry")) {
							isRetry = true;
						}
						if (oneLine.contains("status: 200")) {
							isPush = true;
						}
					})
					.filter(oneLine -> lstErrorIdList.stream().anyMatch(oneLine::contains))
					.collect(Collectors.toList());
			return lstDataList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
		public List<String> getLogRequeStrings(List<String> allDataList) {
			List<String> lstRequestString = new ArrayList<>();
			List<String> lstErrorRequestStrings = getLogStrings(allDataList);
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
	
	 private List<Request> AddRequestToDB(List<String> allDataList) {
	        try {
	        	List<String> data = getLogRequeStrings(allDataList);
	        	
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
				            request.setRetry(isRetry);
				            request.setPush(isPush);
	
				            requestList.add(request);
			 	            
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
	 	           
	            });
	           return requestList;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
}
