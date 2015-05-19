package javaserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.*;

/**
 *
 * @author Brad Minogue
 */
public class IndexHandler  implements HttpHandler{
    @Override
    public void handle(HttpExchange he) throws IOException {
        //a catch all response for anything but the pages we are already
        //handling and for the index
        if(!he.getRequestURI().getPath().endsWith("/"))
        {
            new PageNotFoundHandler().handle(he);
            return;
        }
        Headers header = he.getRequestHeaders();
        String response = Definitions.S_HTML;
        try{
            //Add all of the headers to a JSONObject
            Set<Map.Entry<String, List<String>>> params = 
                    he.getRequestHeaders().entrySet();
            JSONObject obj = new JSONObject();
            for(Map.Entry<String, List<String>> part : params)
            {
                //For every value can contain an array of subvalues/keys
                JSONArray subValue = new JSONArray();
                subValue.add(part.getValue().toString());
                obj.put(part.getKey(), subValue);
            }
            boolean hasSsl = checkSsl(obj);
            if(!hasSsl)
            {
                //ask the user to generate a key
                response += Definitions.ENDL + 
                    "Please install the JustChap app to use this service."
                        +" To play with client-SSL certificates, start"
                        + "by <a href=\"/keygen\">generating a key </a>";
            }
            else if (hasSsl)
            {
                response += "Contains ssl";
            }
        }
        catch(Exception e)
        {
            response += Definitions.ENDL + e.toString();
        }
        //send back response
        response += Definitions.E_HTML;
        he.sendResponseHeaders(200, response.length());
        OutputStream oout = he.getResponseBody();
        oout.write(response.getBytes());
        oout.close();
    }
    boolean checkSsl(JSONObject obj)
    {
        boolean retVal =  false;
        if(obj.containsKey("Client-Verify"))
        {
            JSONArray values = (JSONArray) obj.get("Client-Verify");
            String subValues = (String) values.get(0);
            retVal = values != null && subValues.equalsIgnoreCase("[SUCCESS]");
        }
        return retVal;
    }
    
}
