package com.pack.tools.novdl.net;

import java.util.List;

import com.pack.tools.novdl.service.api.Operation;
import com.pack.tools.novdl.service.api.Service;

public class CliService implements Service, Runnable {
    private String operationNames[]={"quit" , "print" ,"update" ,"shutdown"};
    
    public void start(){
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public String getOperationName() {
        return null;
    }

    @Override
    public Operation[] getSupportedOperations() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getSupportedOperationNames() {
        return operationNames;
    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public List<Operation> getOperationsList() {
        return null;
    }

    @Override
    public void run() {
        
    }

}
