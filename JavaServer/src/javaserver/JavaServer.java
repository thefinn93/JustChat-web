package javaserver;

import java.io.IOException;
import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;

/**
 * @author Brad Minogue
 */
public class JavaServer {
    /**
     * @param args Pass in desired port, otherwise it uses port 5673
     */
    public static void main(String[] args) throws IOException {
        int socketPort = 5673;
        if(args.length > 0)
        {
            try
            {
                socketPort = Integer.parseInt(args[0]);
            }
            catch(NumberFormatException nfe)
            {
                System.out.println(nfe);
            }
        }
        HttpServer server = 
                HttpServer.create(new InetSocketAddress(socketPort),0);
        //Create pages to listen on and handling
        server.createContext("/git", new GitHandler());
        server.setExecutor(null);
        server.start();
    }
}
