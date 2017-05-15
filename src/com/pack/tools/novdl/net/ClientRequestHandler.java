package com.pack.tools.novdl.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientRequestHandler implements Runnable {

    private Socket socket;
    private CommandHandler cmdHandler;

    public ClientRequestHandler(Socket socket , CommandHandler cmdHandler) {
        this.socket = socket;
        this.cmdHandler = cmdHandler;
    }

    @Override
    public void run() {
        if (socket != null) {
            try {
                BufferedReader client=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String command = null;
                while (!socket.isClosed()) {
                    while((command=client.readLine())!=null){
                        if(command.trim().length()>0){
                            cmdHandler.handleCommand(command);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
