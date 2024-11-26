package com.workify.auth.service;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class SocketServerRunner {

    @Autowired
    private SocketIOServer server;

    @PostConstruct
    public void startServer() {
        server.start();
    }

    @PreDestroy
    public void stopServer() {
        server.stop();
    }
}

