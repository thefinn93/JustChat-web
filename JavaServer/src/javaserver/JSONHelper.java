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
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

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
            subValue.put(part.getValue());
            obj.put(part.getKey(), subValue);
        }
        return obj;
    }
    public static JSONObject convertToJson(InputStream body)
    {
        JSONObject obj = new JSONObject();
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
            obj = new JSONObject(sb.toString());
        } catch (JSONException ex) {
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
            subValue.put(part.getValue());
            obj.put(part.getKey(), subValue);
        }
        return obj;
    }
    public static boolean checkSsl(JSONObject obj)
    {
        boolean retVal =  false;
        if(obj.has("Client-verify"))
        {
            JSONArray values = (JSONArray) obj.get("Client-verify");
            LinkedList subValues = (LinkedList) values.get(0);
            retVal = values != null && ((String)subValues.get(0))
                    .equalsIgnoreCase("SUCCESS");
        }
        return retVal;
    }
}
