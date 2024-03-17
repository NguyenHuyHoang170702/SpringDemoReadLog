package com.demo.springdemo.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import com.demo.springdemo.entity.Request;
import com.demo.springdemo.service.RequestService;

@Controller
public class RequestController {
    @Autowired
    private RequestService requestService;

    
  //test @Scheduled(cron = "*/10 * * * * *")
   // @Scheduled(cron = "0 5 * * * *")

    public void addAllRequest() {
    	String forderPath = requestService.copyFilesFromRemoteToLocal();
		List<String> allDataList = new ArrayList<>();
		try {
			Files.walk(Paths.get(forderPath)).filter(Files::isRegularFile).forEach(path -> {
				String fileName = path.getFileName().toString();
				if (fileName.endsWith(".log")) {
					// System.out.println("this is log file: "+ fileName);
					requestService.readLogFile(path, allDataList);

				} else if (fileName.endsWith(".zip") || fileName.endsWith(".rar")) {
					requestService.readLogZipFile(path.toFile(), allDataList);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
	
			List<Request> requests =  requestService.AddRequestToDB(allDataList);
			requestService.saveAll(requests);
			
		}
    }


}
