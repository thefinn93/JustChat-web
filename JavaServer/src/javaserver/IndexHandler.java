/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONObject;

/**
 *
 * @author FryedMan
 */
public class IndexHandler  implements HttpHandler{
    @Override
    public void handle(HttpExchange he) throws IOException {
        if(!he.getRequestURI().getPath().endsWith("/"))
        {
            new PageNotFoundHandler().handle(he);
            return;
        }
        Headers header = he.getRequestHeaders();
        String response = Definitions.S_HTML;
        try{
            Set<Map.Entry<String, List<String>>> params = he.getRequestHeaders().entrySet();
            JSONObject obj = new JSONObject();
            for(Map.Entry<String, List<String>> part : params)
            {
                obj.put(part.getKey(), part.getValue());
            }
            response += obj.toString() + Definitions.ENDL;
            boolean hasSsl = checkSsl(obj);
            if(!hasSsl)
            {
                response += Definitions.ENDL + 
                    "Please install the JustChap app to use this service."
                        +" To play with client-SSL certificates, start"
                        + "by <a href=\"/keygen\">generating a key </a>";
            }
            else if (hasSsl)
            {
                response = "Contains ssl";
            }
        }
        catch(Exception e)
        {
            response += Definitions.ENDL + e.toString();
        }
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
            List values =(List)obj.get("Client-Verify");
            retVal = values != null && !values.contains("SUCCESS");
        }
        return retVal;
    }
    
}
