package com.atypon.nodes.mappernode;

import java.util.List;
import java.util.Map;

public interface Shuffler<T, E> {
    List<Map<T, E>> shuffle(Map<T, E> map);
}
