package com.atypon.nodes.receivers;

public interface Receiver {
    <T> T start(int port);
}
