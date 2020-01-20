package com.atypon.nodes.senders;

public interface Sender {
    <T> void send(String address, int port, T dataToSend);
}
