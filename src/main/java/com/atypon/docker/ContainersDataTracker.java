package com.atypon.docker;

import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ContainersDataTracker {
  private static ContainersDataTracker containersDataTracker = null;

  @Setter
  @Getter
  private AtomicInteger numOfContainers;
  @Setter
  @Getter
  private AtomicInteger numOfMappers;
  @Setter
  @Getter
  private AtomicInteger numOfReducer;
  @Setter
  @Getter
  private AtomicInteger runningContainers;
  @Getter
  private AtomicInteger currentMappersRunning;
  @Getter
  private AtomicInteger currentReducersRunning;
  @Getter
  private AtomicInteger finishedMappers;

  @Getter
  private ArrayList<String> mappersAddresses;
  @Getter
  private ArrayList<String> reducersAddresses;

  @Getter
  private CountDownLatch finishedMappersLatch;
  @Getter
  private CountDownLatch waitForContainersLatch;

  private ContainersDataTracker() {
    mappersAddresses = new ArrayList<>();
    reducersAddresses = new ArrayList<>();
    numOfContainers = new AtomicInteger();
    numOfMappers = new AtomicInteger();
    numOfReducer = new AtomicInteger();
    runningContainers = new AtomicInteger();
    currentMappersRunning = new AtomicInteger();
    currentReducersRunning = new AtomicInteger();
    finishedMappers = new AtomicInteger();
  }

  public static ContainersDataTracker getInstance() {
    if (containersDataTracker == null) {
      containersDataTracker = new ContainersDataTracker();
    }

    return containersDataTracker;
  }

  public void setFinishedMappersLatch(int numOfMappers) {
    finishedMappersLatch = new CountDownLatch(numOfMappers);
  }

  public void setWaitForContainersLatch(int numOfContainers) {
    waitForContainersLatch = new CountDownLatch(numOfContainers);
  }

  public void incrementRunningContainers() {
    this.runningContainers.getAndIncrement();
    waitForContainersLatch.countDown();
  }

  @Synchronized
  public void addMapperAddress(String address) {
    mappersAddresses.add(address);
  }

  @Synchronized
  public void addReducerAddress(String address) {
    reducersAddresses.add(address);
  }

  public void incrementFinishedMappers() {
    this.finishedMappers.getAndIncrement();
    finishedMappersLatch.countDown();
  }

  public void incrementRunningMappers() {
    this.currentMappersRunning.getAndIncrement();
  }

  public void incrementRunningReducers() {
    this.currentReducersRunning.getAndIncrement();
  }
}
