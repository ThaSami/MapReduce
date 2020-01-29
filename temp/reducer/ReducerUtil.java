import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReducerUtil {

  public static Map<?, ?> reduce(Map<Object, List<Object>> map) {
    Map<Object, Object> secondMap = new TreeMap<>();

    map.forEach(
        (k, v) -> {
          int sum = v.stream().mapToInt(entry -> (int) entry).sum();
          secondMap.put(k, sum);
        });
    return secondMap;
  }
}
