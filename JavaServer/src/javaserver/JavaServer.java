package javaserver;

import java.io.IOException;
import com.sun.net.httpserver.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * JavaServer is a server used for JustChat application for andriod and
 * processes requests
 * @author Brad Minogue
 */
public class JavaServer {
    private static final  String[] contextList = {"/", "/git","/api"};
    private static final HttpHandler[] contextHandlerList = {
        new IndexHandler(),
        new GitHandler(),
        new ApiHandler()
    };
    /**
     * @param args Pass in desired port, otherwise it uses port 5673
     */
    public static void main(String[] args) {
        //System.out.println(Definitions.WAKEUP_MESSAGE);
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
        HttpServer server = null;
        try
        {
            server = HttpServer.create(
                    new InetSocketAddress(InetAddress.getLoopbackAddress(),
                        socketPort),0);
            for(int i = 0; i < contextList.length; i++)
            {
                server.createContext(contextList[i], contextHandlerList[i]);
            }
            server.setExecutor(null);
            server.start();
        }
        catch(IOException ioe)
        {
            System.out.println(ioe);
        }
        finally
        {
            if(server == null)
                System.out.println("ERROR");
            else
            {
                //System.out.println(Definitions.GOOD_STARTUP_MESSAGE);
            }
        }
    }
}
