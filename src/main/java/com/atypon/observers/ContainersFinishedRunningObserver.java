package com.atypon.observers;

public class ContainersFinishedRunningObserver extends Observer {

    public ContainersFinishedRunningObserver(Subject subject) {
        this.subject = subject;
        this.subject.attach(this);
    }

    @Override
    public void onUpdate() {

    }
}
