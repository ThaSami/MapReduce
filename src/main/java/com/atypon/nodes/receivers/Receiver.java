package com.atypon.nodes.receivers;

import java.util.List;

public interface Receiver {
    <T> T start(int port);
}
