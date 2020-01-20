package com.atypon.commands;

import com.atypon.workflow.phase.Executor;
import com.atypon.workflow.phase.Rollback;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.Socket;

public class CommandsHandler {

    public static void execute(String command, Socket socket) {

        try {
            Class<?> klass = Class.forName(command);
            Constructor<?> constructor = klass.getDeclaredConstructor(String.class);
            Command cmd;
            if (socket != null) {
                cmd = (Command) constructor.newInstance(socket);
            } else {
                cmd = (Command) constructor.newInstance();
            }

            cmd.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
