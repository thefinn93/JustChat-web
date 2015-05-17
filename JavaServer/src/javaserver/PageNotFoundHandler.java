
package javaserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Brad Minogue
 */
public class PageNotFoundHandler implements HttpHandler{

    @Override
    public void handle(HttpExchange he) throws IOException {
        
        String response = Definitions.S_HTML;
        response += "Page not found";
        response += Definitions.E_HTML;
        he.sendResponseHeaders(200, response.length());
        OutputStream oout = he.getResponseBody();
        oout.write(response.getBytes());
        oout.close();
    }
    
}
