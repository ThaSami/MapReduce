package com.atypon.nodes.mappernode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashShuffler<T, E> implements Shuffler<T, E> {

    private List<Map<T, E>> shuffleResult;
    private Map<T, E> mapToShuffle;
    int numbOfNodes;

    public HashShuffler(Map<T, E> mapToShuffle, int numbOfNodes) {
        this.numbOfNodes = numbOfNodes;
        this.mapToShuffle = mapToShuffle;
        shuffleResult = new ArrayList<>(numbOfNodes);

        for (int i = 0; i < numbOfNodes; i++) {
            shuffleResult.add(i, new HashMap());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<T, E>> shuffle(Map<T, E> map) {
        System.out.println("Started Shuffling");

        for (T k : mapToShuffle.keySet()) {
            shuffleResult.get(Math.abs(k.hashCode()) % numbOfNodes).put(k, mapToShuffle.get(k));
        }
        System.out.println("Shuffling Finished");
        return shuffleResult;

    }
}

