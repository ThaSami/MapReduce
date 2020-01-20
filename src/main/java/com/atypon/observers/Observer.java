package com.atypon.observers;

public abstract class Observer {
    protected Subject subject;

    public abstract void onUpdate();
}