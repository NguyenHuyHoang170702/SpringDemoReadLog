package com.demo.catch_error_service.service.file;


import com.demo.catch_error_service.service.server.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class FileServiceImpl implements FileService{
    @Autowired
    private ServerService serverService;

    @Override
    public List<String> allFilesData() {
        String localFilePath = this.serverService.copyFilesFromRemoteToLocal(this.serverService.sshToServer());
        List<String> allDataList = new ArrayList<>();
        try {
            Files.walk(Paths.get(localFilePath)).filter(Files::isRegularFile).forEach(path ->{
                String fileName  = path.getFileName().toString();
                if (fileName.endsWith(".log")){
                    readLogFile(path,allDataList);
                }else if(fileName.endsWith(".zip")){
                    readLogZipFile(path.toFile(), allDataList);
                }
            });
            return allDataList;
        }catch (Exception e){
            log.error("error to read all data");
        }
        return null;
    }

    public List<String> readLogFile(Path path, List<String> allDataList) {
        try (var oneLineData = Files.lines(path)) {
            oneLineData.forEach(item -> {
                allDataList.add(item);
            });
            return allDataList;
        } catch (IOException e) {
            log.error("error to read log file");
        }
        return null;
    }

    public List<String> readLogZipFile(File file, List<String> allDataList) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().endsWith(".log")) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipInputStream));
                    String lineString;

                    while ((lineString = bufferedReader.readLine()) != null) {
                        var oneLineData = lineString.split("\n");
                        for (String data : oneLineData) {
                            allDataList.add(data);
                        }
                    }
                    return allDataList;
                }
            }
        } catch (IOException e) {
            log.error("error to read zip file");
        }
        return null;
    }
}
