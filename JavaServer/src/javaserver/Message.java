package javaserver;

import java.util.Date;

/**
 * @author Brad Minogue
 */
public class Message {
    final User user;
    final String message;
    final Date date;
    Message(User usr, String msg, Date timeStamp)
    {
        user = usr;
        message = msg;
        date = timeStamp;
    }
}
