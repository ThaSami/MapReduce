
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.List;
 import java.io.*;
 public class MapperUtil { 
public static Map<?,?> mapping(String file){ 
       Pattern pattern = Pattern.compile("[a-zA-Z]+");
        TreeMap<String,Integer> wordCount = new TreeMap<String,Integer>();

        try (
                BufferedReader src = new BufferedReader(new FileReader(file));

        ){

            Matcher matcher ;
            String str = src.readLine();
            while(str!=null){
                if(!str.equals("")){
                    matcher = pattern.matcher(str);
                    while(matcher.find()){
                        String word = matcher.group();
                        if(!wordCount.containsKey(word))
                            wordCount.put(word,1);
                        else
                            wordCount.put(word,wordCount.get(word)+1);
                    }
                }
                str = src.readLine();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
return wordCount;
} }