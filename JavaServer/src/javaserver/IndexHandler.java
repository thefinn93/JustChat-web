package javaserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import org.json.*;

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
            JSONObject obj = JSONHelper.convertToJson(he.getRequestHeaders());
            if(!JSONHelper.checkSsl(obj))
            {
                //ask the user to generate a key
                response += Definitions.ENDL +
                    "Please install the JustChap app to use this service."
                        +" To play with client-SSL certificates, start"
                        + "by <a href=\"/keygen\">generating a key </a>";
            }
            else
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

}
