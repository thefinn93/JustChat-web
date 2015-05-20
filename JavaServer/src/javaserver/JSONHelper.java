package javaserver;

import com.sun.net.httpserver.Headers;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Brad Minogue
 */

public class JSONHelper {
    public static JSONObject convertToJson(Set<Map.Entry<String, List<String>>> set)
    {
        JSONObject obj = new JSONObject();
        for(Map.Entry<String, List<String>> part : set)
        {
            //For every value can contain an array of subvalues/keys
            JSONArray subValue = new JSONArray();
            subValue.add(part.getValue());
            obj.put(part.getKey(), subValue);
        }
        return obj;
    }
    public static JSONObject convertToJson(InputStream body)
    {
        JSONObject obj = new JSONObject();
        JSONParser parser = new JSONParser();
        Object o = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(body));
        StringBuilder sb = new StringBuilder();
        String line = "";
        try {
            while((line = br.readLine()) != null)
            {
                sb.append(line);
            }
        } catch (IOException ex) {
        }
        finally{
            if(br != null)
                try {
                    br.close();
            } catch (IOException ex) {
             }
        }
        try {
            o  = parser.parse(sb.toString());
        } catch (ParseException ex) {
        }
        return (JSONObject)o;
    }
    public static JSONObject convertToJson(Headers h)
    {
        JSONObject obj = new JSONObject();
        Set<Map.Entry<String, List<String>>> params = 
                h.entrySet();
        for(Map.Entry<String, List<String>> part : params)
        {
            //For every value can contain an array of subvalues/keys
            JSONArray subValue = new JSONArray();
            subValue.add(part.getValue());
            obj.put(part.getKey(), subValue);
        }
        return obj;
    }
    public static boolean checkSsl(JSONObject obj)
    {
        boolean retVal =  false;
        if(obj.containsKey("Client-verify"))
        {
            JSONArray values = (JSONArray) obj.get("Client-verify");
            LinkedList subValues = (LinkedList) values.get(0);
            System.out.println(subValues.get(0));
            retVal = values != null && ((String)subValues.get(0))
                    .equalsIgnoreCase("SUCCESS");
        }
        return retVal;
    }
}
