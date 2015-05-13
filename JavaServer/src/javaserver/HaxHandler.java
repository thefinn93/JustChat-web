package javaserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;

/**
 * Handles the request for location /hax
 * @author Finn Herzfeld
 */
class HaxHandler implements HttpHandler {

    public HaxHandler() {
    }
    /**
     * Function used to hack the planet
     * @param the HTTP Exchange variable
     * @throws IOException when unable to respond
     */
    @Override
    public void handle(HttpExchange he) throws IOException {
        String response = "Fuck off";
        InetAddress tempAddr = he.getRemoteAddress().getAddress();
        name = he.getRequestHeader('Client-S-DN');
        response = "Hello, " + name + "\n";
        he.sendResponseHeaders(200, response.length());
        OutputStream oout = he.getResponseBody();
        oout.write(response.getBytes());
        oout.close();
    }
    /**
     * Prints out response from executing command
     * @param inputSource response from executing command
     * @throws IOException
     */
    private String outPutProccessOutput(Process inputSource) throws IOException
    {
        String retVal = "";
        BufferedReader stdInput = new BufferedReader(
                new InputStreamReader(inputSource.getInputStream()));
        BufferedReader stdError = new BufferedReader(
                new InputStreamReader(inputSource.getErrorStream()));
        String tempVal = "";
        while((tempVal = stdInput.readLine()) != null)
        {
            retVal +=tempVal;
        }
        tempVal = "";
        while((tempVal = stdError.readLine()) != null)
        {
            retVal +=tempVal;
        }
        return retVal;
    }

}
