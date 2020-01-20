package com.atypon.docker;

import com.atypon.utility.Constants;
import com.atypon.utility.FilesUtil;

import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class ContainersHandler {
    private static ContainersHandler containersDataHandler = null;
    private ContainersDataTracker containersDataTracker;

    private ContainersHandler() {
        containersDataTracker = ContainersDataTracker.getInstance();
    }

    public static ContainersHandler getInstance() {
        if (containersDataHandler == null) {
            containersDataHandler = new ContainersHandler();
        }

        return containersDataHandler;
    }


    public void sendReducerAddressesToMappers() throws InterruptedException {
        Thread.sleep(3000);
        for (String address : containersDataTracker.getMappersAddresses()) {
            Thread t =
                    new Thread(
                            () -> {
                                try (Socket sk =
                                             new Socket(address, Constants.MAPPERS_REDUCERADDRESS_RECEIVER_PORT)) {
                                    ObjectOutputStream objectOutput = new ObjectOutputStream(sk.getOutputStream());
                                    objectOutput.writeObject(containersDataTracker.getReducersAddresses());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
            t.start();
            t.join();
        }
    }

    public void sendNumOfMappersToReducers() {
        for (String reducerAddress : containersDataTracker.getReducersAddresses()) {
            try (Socket socket = new Socket(reducerAddress, Constants.REDUCER_RECEIVER_PORT);
                 OutputStream outputStream = socket.getOutputStream();
                 DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
                dataOutputStream.writeUTF(String.valueOf(containersDataTracker.getNumOfMappers()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendFilesToMappers(String rootDirectory) {
        List<String> filesAbsPath = FilesUtil.getFilesAbsPathInDirectory(rootDirectory);
        int i = 0;
        for (String fileAbsPath : filesAbsPath) {
            System.out.println("Sending : " + fileAbsPath);
            FilesUtil.fileUploader(containersDataTracker.getMappersAddresses().get(i), fileAbsPath);
            System.out.println(containersDataTracker.getMappersAddresses().get(i));
            i++;
        }
    }


    public void sendStartFlagToReducers() throws InterruptedException {
        Thread.sleep(3000);
        for (String reducerAddress : containersDataTracker.getReducersAddresses()) {
            try (Socket socket = new Socket(reducerAddress, Constants.REDUCER_RECEIVER_PORT);
                 OutputStream outputStream = socket.getOutputStream();
                 DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
                dataOutputStream.writeUTF("start");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
