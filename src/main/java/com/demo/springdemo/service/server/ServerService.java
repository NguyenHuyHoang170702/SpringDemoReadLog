package com.demo.springdemo.service.server;

import com.jcraft.jsch.Session;

public interface ServerService {
    public Session sshToServer();
    public String copyFilesFromRemoteToLocal(Session session);
}
