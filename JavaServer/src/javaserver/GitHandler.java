package javaserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;

/**
 * Handles the request for location /git
 * @author Brad Minogue
 */
class GitHandler implements HttpHandler {
    
    private static final String GIT_SOURCE = 
            "https://github.com/thefinn93/JustChat-web.git";

    public GitHandler() {
    }
    /**
     * Function used to pull from git
     * @param he HTTP Exchange variable
     * @throws IOException when unable to respond
     */
    @Override
    public void handle(HttpExchange he) throws IOException {
        String response = "Fuck off";
        InetAddress tempAddr = he.getRemoteAddress().getAddress();
        if(tempAddr.isLoopbackAddress() || tempAddr.isAnyLocalAddress())
        {
            response = "welcome";
            try
            {
                Process responceFromCommand = Runtime.getRuntime().exec(
                        "git pull " + GIT_SOURCE);
                response = outPutProccessOutput(responceFromCommand);
            }
            catch(Exception e)
            {
                response = e.toString();
            }
        }
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
