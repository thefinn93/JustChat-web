package javaserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author Brad Minogue
 */
public class ServerThread extends Thread{
    Socket socket;
    BufferedReader bin;
    private static final String GIT_SOURCE = 
            "https://github.com/minogb/JustChat-web.git";
    private final String GIT_COMMAND = "git";
    /**
     * @param connection socket we get commands from
     * @throws IOException fatal error
     */
    public ServerThread(Socket connection) throws IOException
    {
        socket = connection;
        this.start();
    }
    /**
     * Waits for input and tests for commands
     * @throws IOException fatal error
     */
    private void waitForMessage() throws IOException
    {
        bin = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        while(true)
        {
            String message = bin.readLine();
            System.out.println("" + socket + ": " + message);
            if(message.equals(GIT_COMMAND))
            {
                Process responceFromCommand = Runtime.getRuntime().exec(
                        "git pull " + GIT_SOURCE);
            }
        }
    }
    /**
     * Prints out response from executing command
     * @param inputSource response from executing command
     * @throws IOException 
     */
    private void outPutProccessOutput(Process inputSource) throws IOException
    {
        BufferedReader stdInput = new BufferedReader(
                new InputStreamReader(inputSource.getInputStream()));
        BufferedReader stdError = new BufferedReader(
                new InputStreamReader(inputSource.getErrorStream()));
        String tempVal = "";
        while((tempVal = stdInput.readLine()) != null)
        {
            System.out.println(tempVal);
        }
        tempVal = "";
        while((tempVal = stdError.readLine()) != null)
        {
            System.out.println(tempVal);
        }
    }
    @Override
    public void run()
    {
        try
        {
            waitForMessage();
        }
        catch(IOException ioe)
        {
            System.out.println(ioe);
        } finally
        {
            try
            {
                socket.close();
            }
            catch(IOException ioe)
            {
                System.out.println(ioe);
            }
        }
    }
}
