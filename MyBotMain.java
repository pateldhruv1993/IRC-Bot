import org.jibble.pircbot.*;

public class MyBotMain {
    
    public static void main(String[] args) throws Exception {
        
        // Start our bot up.
        MyBot bot = new MyBot();

        // Enable debugging output.
        bot.setVerbose(true);
        
        // Connect to the IRC server.
        bot.connect("irc.freenode.net");
        
        // Join the irc channel.
        bot.joinChannel("#mybottest");

        /*

        //Example of how to make multiple instances of bot and make it join different server or channel

        // Start our bot up.
        MyBot bot = new MyBot();

        // Enable debugging output.
        bot.setVerbose(true);
        
        // Connect to the IRC server.
        bot.connect("irc.freenode.net");
        
        // Join the irc channel.
        bot.joinChannel("#freenode");
        */

    }
}