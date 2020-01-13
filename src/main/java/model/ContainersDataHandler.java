package model;

import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

import java.util.ArrayList;
import java.util.List;

public class ContainersDataHandler {
    @Setter
    @Getter
    public static int numOfContainers = 20;
    public static int runningContainers = 0;
    private List<String> mappersAddresses;
    private List<String> reducersAddresses;

    private static ContainersDataHandler containersDataHandler = null;

    private ContainersDataHandler() {
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

    public static ContainersDataHandler getInstance() {
        if (containersDataHandler == null)
            containersDataHandler = new ContainersDataHandler();

        return containersDataHandler;
    }

    public String getReducersAddresses(int id) {
        return reducersAddresses.get(id);
    }

    @Synchronized
    public void incerementRunningContainers() {
        runningContainers++;
    }

    public void sendFileToMappers() {

    }
}
