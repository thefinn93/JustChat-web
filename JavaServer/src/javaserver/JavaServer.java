package javaserver;

import java.io.IOException;
import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;

/**
 * JavaServer is a server used for JustChat application for andriod and
 * processes requests
 * @author Brad Minogue
 */
public class JavaServer {
    private static  String[] contextList = {"/", "/git", "/hax"};
    private static HttpHandler[] contextHandlerList = {
        new IndexHandler(),
        new GitHandler(),
        new HaxHandler()  // This is basically just finn dicking around with Java
    };
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
        for(int i = 0; i < contextList.length; i++)
        {
            server.createContext(contextList[i], contextHandlerList[i]);
        }
        server.setExecutor(null);
        server.start();
    }
}
