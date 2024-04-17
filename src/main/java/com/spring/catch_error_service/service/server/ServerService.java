package com.spring.catch_error_service.service.server;

import com.jcraft.jsch.Session;

public interface ServerService {
    public Session sshToServer();
    public String copyFilesFromRemoteToLocal(Session session);
}
