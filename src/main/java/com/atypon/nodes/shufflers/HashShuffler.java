package com.atypon.nodes.shufflers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashShuffler implements Shuffler {

  int numbOfNodes;
  private List<Map<Object, Object>> shuffleResult;
  private Map<Object, Object> mapToShuffle;

  public HashShuffler(Map<?, ?> mapToShuffle, int numbOfNodes) {
    this.numbOfNodes = numbOfNodes;
    this.mapToShuffle = (Map<Object, Object>) mapToShuffle;
    shuffleResult = new ArrayList<>(numbOfNodes);

    for (int i = 0; i < numbOfNodes; i++) {
      shuffleResult.add(i, new HashMap<>());
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Map<Object, Object>> shuffle() {
    System.out.println("Started Shuffling");

    for (Object k : mapToShuffle.keySet()) {
      shuffleResult.get(Math.abs(k.hashCode()) % numbOfNodes).put(k, mapToShuffle.get(k));
    }
    System.out.println("Shuffling Finished");
    return shuffleResult;
  }
}
