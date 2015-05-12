package javaserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Brad Minogue
 */
public class JavaServer {
    int socketPort = 5673;
    ServerSocket serverSocket;
    /**
     * @param args Pass in desired port, otherwise it uses port 5673
     */
    public static void main(String[] args) {
        int socketPort = -1;
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
        try
        {
            new JavaServer(socketPort);
        }
        catch(IOException ioe)
        {
            System.out.println(ioe);
        }
    }
    /**
     * @param port desired port to listen on
     * @throws IOException Fatal error
     */
    public JavaServer(int port) throws IOException
    {
        if(port > 0)
            socketPort = port;
        serverSocket = new ServerSocket(socketPort);
        while(true)
        {
            Socket connection = serverSocket.accept();
            new ServerThread(connection);
        }
    }
}
