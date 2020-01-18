
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.List;import java.io.*;
public class ReducerUtil { 

public static Map<?,?> reduce(Map<Object, List<Object>> map){
        Map<Object, Object> secondMap = new TreeMap<>();

        map.forEach((k, v) -> {
            int sum = v.stream().mapToInt(entry ->  (int)entry).sum();
            secondMap.put(k, sum);
        });
        return secondMap;

} }