package com.spring.catch_error_service.service.file;


import com.spring.catch_error_service.service.server.ServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Autowired
    private ServerService serverService;

    @Override
    public List<String> allFilesData() {
        String localFilePath = this.serverService.copyFilesFromRemoteToLocal(this.serverService.sshToServer());
        List<String> allDataList = new ArrayList<>();
        try {
            Files.walk(Paths.get(localFilePath)).filter(Files::isRegularFile).forEach(path -> {
                String fileName = path.getFileName().toString();
                if (fileName.endsWith(".log")) {
                    readLogFile(path, allDataList);
                } else if (fileName.endsWith(".zip")) {
                    readLogZipFile(path.toFile(), allDataList);
                }
            });
            return new ArrayList<>(allDataList);
        } catch (Exception e) {
            log.error("error to read all data");
            return Collections.emptyList();
        }
    }

    public void readLogFile(Path path, List<String> allDataList) {
        try (var oneLineData = Files.lines(path)) {
            oneLineData.forEach(allDataList::add);
        } catch (IOException e) {
            log.error("error to read log file", e);
        }
    }

    public void readLogZipFile(File file, List<String> allDataList) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.getName().endsWith(".log")) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(zipInputStream));
                    String lineString;
                    while ((lineString = bufferedReader.readLine()) != null) {
                        var oneLineData = lineString.split("\n");
                        Collections.addAll(allDataList, oneLineData);
                    }
                }
            }
        } catch (IOException e) {
            log.error("error to read zip file", e);
        }
    }
}
