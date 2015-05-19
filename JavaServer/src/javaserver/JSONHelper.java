package javaserver;

import com.sun.net.httpserver.Headers;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
