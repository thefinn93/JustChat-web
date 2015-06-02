package javaserver;

import java.util.Date;

/**
 * @author Brad Minogue
 */
public class Message {
    final String user;
    final String message;
    final Date date;
    Message(String usr, String msg, Date timeStamp)
    {
        user = usr;
        message = msg;
        date = timeStamp;
    }
}
