package model;

import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ContainersHandler {
    @Setter
    @Getter
    public static int numOfContainers = 20;
    private List<String> mappersAddresses;
    private List<String> reducersAddresses;

    private static ContainersHandler containersHandler = null;

    private ContainersHandler() {
        this.mappersAddresses = new ArrayList<>();
        this.reducersAddresses = new ArrayList<>();
    }

    @Synchronized
    public void addMapperAddress(String address) {
        mappersAddresses.add(address);
    }

    @Synchronized
    public void addReducerAddress(String address) {
        reducersAddresses.add(address);
    }

    public static ContainersHandler getInstance() {
        if (containersHandler == null)
            containersHandler = new ContainersHandler();

        return containersHandler;
    }

    public String getReducersAddresses(int id) {
        return reducersAddresses.get(id);
    }
}
