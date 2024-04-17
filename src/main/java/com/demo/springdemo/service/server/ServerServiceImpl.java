package com.demo.springdemo.service.server;


import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

@Service
@Slf4j
public class ServerServiceImpl implements ServerService {

    @Value("${sftp.username}")
    private String remoteUsername;
    @Value("${sftp.password}")
    private String password;
    @Value("${sftp.host}")
    private String remoteHost;
    @Value("${dir.localDirInbox}")
    private String localFilePath;
    @Value("${dir.remoteDir}")
    private String remoteFilePath;

    @Override
    public Session sshToServer() {
        try {
            JSch jSch = new JSch();
            Session session = jSch.getSession(remoteUsername, remoteHost, 22);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            if (session.isConnected()){
                log.info("Connected to server successfully!");
                return session;
            }
        }catch (Exception e){
            log.error("ssh to server: "+ e.getMessage());
        }
        return null;
    }

    @Override
    public String copyFilesFromRemoteToLocal(Session session) {
        try {
            if(session != null && session.isConnected()){
                ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
                channelSftp.connect();

                if (channelSftp.isConnected()){
                    channelSftp.cd(remoteFilePath);

                    Vector<ChannelSftp.LsEntry> listFile = channelSftp.ls("*");

                    listFile.forEach(lsEntry -> {
                        if(!lsEntry.getAttrs().isDir()){
                            String remoteFileName = lsEntry.getFilename();
                            try (InputStream remoteFileInputStream = channelSftp.get(remoteFileName)) {
                                File localFile = new File(localFilePath, remoteFileName);
                                FileOutputStream localFileOutputStream = new FileOutputStream(localFile);
                                byte[] buffer = new byte[1024];
                                int byteRead;
                                while ((byteRead = remoteFileInputStream.read(buffer)) != -1){
                                    localFileOutputStream.write(buffer, 0, byteRead);
                                }
                                remoteFileInputStream.close();
                                localFileOutputStream.close();
                            } catch (SftpException e) {
                                throw new RuntimeException(e);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else{
                            log.error("error to read folder");
                        }
                    });
                    if(!listFile.isEmpty()){
                        channelSftp.disconnect();
                        log.info("copy completed, disconnect sftp");
                        return localFilePath;
                    }else {
                        log.error("folder is empty");
                    }

                }else {
                    log.error("error to connect to sftp");
                }
            }
        }catch (Exception e){
            log.error("copyFilesFromRemoteToLocal: "+ e.getMessage());
        }
        return null;
    }
}
