package com.pack.tools.novdl.net;

import java.util.List;

import com.pack.tools.novdl.service.api.Operation;
import com.pack.tools.novdl.service.api.Service;
import com.pack.tools.novdl.service.api.ServiceProvider;

public class CliServiceProvider implements ServiceProvider{
    
    private String operations[]={"Cli"};
    private String serviceNames[]={"cli" ,"quit", "print" , "update" , "shutdown"};

    @Override
    public Operation[] getSupportedOperations() {
        return null;
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
    public String getOperationName() {
        return null;
    }

    @Override
    public Service[] getSupportedServices() {
        return null;
    }

    @Override
    public String[] getSupportedOperationNames() {
        return operations;
    }

    @Override
    public String[] getSupportedServiceNames() {
        return serviceNames;
    }

}
