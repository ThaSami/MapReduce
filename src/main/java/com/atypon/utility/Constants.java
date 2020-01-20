package com.atypon.utility;

public class Constants {
    // ports
    public static final int MAIN_SERVER_PORT = 7777;
    public static final int TREE_MAP_RECEIVER_PORT = 8787;
    public static final int REDUCER_RECEIVER_PORT = 8789;
    public static final int MAPPERS_REDUCERADDRESS_RECEIVER_PORT = 9090;
    public static final int MAPPERS_FILE_RECEIVER_PORT = 6666;
    public static final int COLLECTOR_PORT = 9999;
    public static final String HOST_IP_ADDRESS = "192.168.8.102";


    private Constants() {
    }

    static class Messeges {
        public static String error = "";
    }
}
