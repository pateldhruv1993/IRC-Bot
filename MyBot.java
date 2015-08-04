import org.jibble.pircbot.*;
import java.io.*;
import java.net.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.net.MalformedURLException;
import java.security.cert.Certificate;
import java.io.*;
import java.net.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class MyBot extends PircBot {

    ///////////////////////////////////////////////////////////////////////////
    //                          Settings
    ///////////////////////////////////////////////////////////////////////////

    // Change this value if you the program to make more request to the imdbapi.com for !randomMovie <genre> command
    private int maxAPIrequest = 40;

    //Change this to the name of whoever you want to be able to delete recommentdations and output custom bot messages.
    private String adminsUserName = "AdminsUserName";

    //Change this value if you want to change the max number of messages the bot should store for the !sup command
    private int maxMessagesOnStack = 50;









    public static String sourceData = "";
    public static String newUrl;
    public static String currentChan;
    List<String> msgStake = new LinkedList<String>();

    public MyBot() {

        //Set the name and login of the bot (You can use it in MyBotMain if you want your individual instances of bot to have different names)
        this.setName("BreeDUH");
        this.setLogin("BreeDUH");

        setMessageDelay(500);
    }

    public void onJoin(String channel, String sender, String login, String hostname) {
        //Set the class variable 'currentChan' to the parameter channel
        currentChan = channel;
    }

    //Public message command handling (All the message in a channel)
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        int ranCmd = 0;     // Just a variable to see how many commands were handled by the bot

        //Shows current system time
        if (message.matches("^!time")) {
            showCurrentSysTime(message, channel, sender, login, hostname);
            ranCmd++;
        }

        //Shows random movie using imdb's random movie generator and only shows them if they meet the users genere criteria
        else if (message.equals("!randomMovie") || message.indexOf("!randomMovie ") == 0) {

            randomMovie(message, channel, sender, login, hostname);
            ranCmd++;

        }

        //Shows last 100 msgs of the channel
        else if (message.equals("!sup") || message.equals("!sup ")) {

            showLastMsgs(sender);

        }

        //Outputs a random number between 0 - <user defines num> OR <userdefined num> - <userdefined num>
        else if (message.indexOf("!num ") == 0) {
            message = message.trim();
            randomNumber(message, channel, sender, login, hostname);
            ranCmd++;

        }

        // Finds out a movie that a user recommended. !rec will show a recommendation where as !rec <link> will add the recommendation to a file
        else if (message.indexOf("!rec") == 0) {
            recommendation(message, channel, sender, login, hostname);
            ranCmd++;

        }

        //Cool story bro
        else if (message.equals("!csb") || message.equals("!csb ")) {
            coolStoryBro(message, channel, sender, login, hostname);
            ranCmd++;

        }

        //Google something. Generates a google search link
        else if (message.indexOf("!g ") == 0) {
            googleSearch(message, channel, sender, login, hostname);
            ranCmd++;

        }


        //Delete a bad/troll recommendaton. Will only let some predefined user to run the command
        else if (message.indexOf("!delrec ") == 0) {
            message = message.trim();
            deleteRecommendation(message, channel, sender, login, hostname);
            ranCmd++;
        }



        //Link commands
        //Links for rules
        else if (message.matches("!rules")) {
            sendMessage(channel, "Important Rules");
            sendMessage(channel, "Upload Rules: http://example.com/upload");
            sendMessage(channel, "Co-Existing: http://example.com/co-exist");
            sendMessage(channel, "Trumping: http://example.com/trmp");
            ranCmd++;

        }
        //Manual for how to use the bot
        else if (message.matches("!man ") || message.matches("!man")) {
            msgHelpManual(sender);
            ranCmd++;
        }

        //Add the message to the call stack
        if(ranCmd == 0){
            msgStake.add(Colors.BOLD + "<" + currentTime() + "> ["+ sender + "] " + Colors.NORMAL + message);
            if(msgStake.size() > maxMessagesOnStack){
                msgStake.remove(0);
            }
        }
    }

    //Private Message command handling
    public void onPrivateMessage(String sender, String login, String hostname, String message) {
        String channel = sender;
        if (sender.equals(adminsUserName) {
          System.out.println("ADMIN sending private messages.");
            sendMessage(currentChan, message);
        } else{
          System.out.println("Random user sending private messages.");
        }
    }



    //Method to extract the imdb link from the html data comming from imdb.com/random
    public String randomImdbExt(String html) {
        String imdbLink = "";
        int start = html.indexOf("<link rel=\"canonical\" href=\"") + 28;
        imdbLink = html.substring(start, html.indexOf("\"", start + 16));
        return imdbLink;
    }

    //Determinins if the json from the api is a movie with particular genre or not
    public boolean hasGenre(String data, String genre) {
        int start = data.indexOf("\"Genre\":\"") + 9;
        String genres = data.substring(start, data.indexOf("\",\"", start));
        if (genres.indexOf(genre) != -1) {
            return true;
        }
        return false;
    }

    //Method to extract the random nubmer from the html data of the random.org
    public String randomNumExt(String html) {
        String randomNum = "";
        int start = html.indexOf("<pre class=\"data\">") + 18;
        randomNum = html.substring(start, html.indexOf("</pre>", start));
        return randomNum;
    }

    //Method to extract imdb and year and present it as Movie (1995)
    public String extImdbNameYear(String data) {
        int start = data.indexOf("{\"Title\":\"") + 10;
        String name = data.substring(start, data.indexOf("\",\"", start));
        start = data.indexOf("\",\"Year\":\"") + 10;
        String year = data.substring(start, data.indexOf("\",\"", start));
        start = data.indexOf("\",\"imdbRating\":\"") + 16 ;
        if(start != -1){
            String rating = data.substring(start, data.indexOf("\",\"", start));
            return name + " (" + year + ") " + Colors.YELLOW + "[" +rating + "]";
        }
        return name + " (" + year + ")";
    }

    //Method to check if the given string is an integer or not
    public boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    //Method to generate a tiny url
    public String urlShortner(String link) {
        return redirectURL("http://is.gd/create.php?format=simple&url=" + link.trim());
    }

    //Method to return a random number between a range, but not as good as random.org
    public int randomBetweenRange(int Min, int Max) {
        return Min + (int) (Math.random() * ((Max - Min) + 1));
    }

    //Method to handle url redirect and stuff. This method produces/changes sourceData variable with the source of the webpage
    public static String redirectURL(String url) {
      try {
        BufferedReader in;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection)obj.openConnection();
        con.setReadTimeout(2000);
        con.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
        con.addRequestProperty("User-Agent", "Mozilla");
        con.addRequestProperty("Referer", "google.com");
        boolean redirect = false;
        newUrl = url;
        // Normally, 3xx is redirect
        int status = con.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
          if (status == HttpURLConnection.HTTP_MOVED_TEMP
                || status == HttpURLConnection.HTTP_MOVED_PERM
                || status == HttpURLConnection.HTTP_SEE_OTHER) {
            redirect = true;
          }
        }

        if (redirect) {

          // get redirect url from "location" header field
          newUrl = con.getHeaderField("Location");
          System.out.println("The redirected URL =====>" +newUrl);



          if(newUrl.indexOf("https://") == 0){
            URL redirectedUrl1 = new URL(newUrl);
            HttpsURLConnection conn = (HttpsURLConnection)redirectedUrl1.openConnection();
            conn.setReadTimeout(2000);
            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla");
            conn.addRequestProperty("Referer", "google.com");
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          }
          else{
            URL redirectedUrl = new URL(newUrl);
            HttpURLConnection conn = (HttpURLConnection)redirectedUrl.openConnection();
            conn.setReadTimeout(2000);
            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla");
            conn.addRequestProperty("Referer", "google.com");
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
          }

        }
        else{
          System.out.println("CALLING BUFFER READER FOR THE NON REDIRECTED URL");
          in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        }


        newUrl = con.getURL().toString();
        String inputLine;
        StringBuffer html = new StringBuffer();
        if (in == null){
          System.out.println("Bufferreader in: IS EMPTY");
        } else{
          System.out.println("Bufferreader in: IS NOT EMPTY");
        }
        while ((inputLine = in.readLine()) != null) {
          html.append(inputLine);
        }
        in.close();
        return html.toString();

      } catch (Exception e) {
        e.printStackTrace();
        return "false";
      }
    }

    //Method to extract title of the page
    public String extTitleOfWebPage(String data) {
        String title = "";
        Pattern p = Pattern.compile("<head>.*?<title>(.*?)</title>.*?</head>", Pattern.DOTALL);
        Matcher m = p.matcher(data);
        while (m.find()) {
            title = m.group(1);
        }
        return title;
    }

    //Function to PM rhe bot manual to the user
    public void msgHelpManual(String sender) {
        sendMessage(sender, "List of commands:");
        sendMessage(sender, "!randomMovie [genre]  - Finds a random movie using imdb.com/random/title and output's the IMDB link for that particular movie. The genre parameter is optional. Currently supported genres -> [Action, Adventure, Comedy, Drama, Mystery, Romance, Sci-Fi, Thriller]");
        sendMessage(sender, "!num <A> [B] - Finds a random number using random.org . If only 1 parameter is given, the random number will be generated between 0 and A. Parameter B is option, if B is given, the random number will be between A and B. The limit is �1,000,000,000");
        sendMessage(sender, "!rec [link] - If no link is given as a prameter, outputs one of the random recommendations by the users. If a link is given, it will add that link to the database with your USERNAME!"+Colors.BOLD+ "ONLY IMDB LINKS ALLOWED. Format (http://www.imdb.com/title/tt0092115/ OR https://www.imdb.com/title/tt0092115/)");
        sendMessage(sender, "!g - Google something from irc! This command will output a google search link, a link to the first page from the results and the title of this first page.");
        sendMessage(sender, "!rules - Links to some important rule.");
        sendMessage(sender, "!sup - Shows you the last 50 messages from the channel.");
        sendMessage(sender, "!time - Shows current time of BOT's system.");
    }

    //Function to read an entire file
    private static String readEntireFile(String filename) throws IOException {
        FileReader in = new FileReader(filename);
        StringBuilder contents = new StringBuilder();
        char[] buffer = new char[4096];
        int read = 0;
        do {
            contents.append(buffer, 0, read);
            read = in.read(buffer);
        } while (read >= 0);
        return contents.toString();
    }

    //Function to add/show a recommendation
    public void recommendation(String message, String channel, String sender, String login, String hostname) {
        int err = 0;
        String fileLoc = "./rec.dat";
        String fileData = "";
        String webpageData = "";
        try {
            fileData = readEntireFile(fileLoc);
            fileData = fileData.trim();
        } catch (Exception e) {
            sendMessage(channel, "A problem occured while reading recommendations file. :/");
            err = 1;
        }

        if (message.equals("!rec") && err == 0) {

            if (fileData != "" || fileData != " " || fileData != null) {
                String[] tokens = fileData.split("\\|");
                int randomNum = Integer.parseInt(randomNumExt(redirectURL("http://www.random.org/integers/?num=1&min=0&max=" + (tokens.length - 1) + "&col=5&base=10&format=html&rnd=new")).trim());
                String[] recommendationRow = tokens[randomNum].split(",");

                String tempLink =  recommendationRow[0].trim();
                int start = tempLink.indexOf("tt", 3);
                String imdbId = tempLink.substring(start, start + 9);
                webpageData = redirectURL("http://www.imdbapi.com/?i=" + imdbId + "&r=json");
                String pageTitle = extImdbNameYear(webpageData);

                sendMessage(channel, pageTitle + " (Recommended by-" + Colors.BOLD + recommendationRow[1] + ")");
                sendMessage(channel, recommendationRow[0].trim());

            } else {
                sendMessage(channel, "There are no recommendations right now. Why don't you be the first!");
            }


        } else if (message.indexOf("!rec ") == 0 && err == 00) {//code executes when a user wants to add a movie
            message = message.trim();
            String link = message.substring(message.indexOf(" ") + 1);

            if (link.indexOf(" ") == -1) {
                if ((link.indexOf("http://www.imdb.com/title/") != -1) || (link.indexOf("https://www.imdb.com/title/") != -1)) {
                    String movieId = "";
                    int start = link.indexOf("title/");
                    int end1 = link.indexOf("/", start + 8);
                    if (end1 == -1) {
                        movieId = link.substring(start + 6);
                    } else {
                        movieId = link.substring(start + 6, end1);
                    }
                    System.out.println("Trying to add movie with movieID:" + movieId);

                    if (fileData.indexOf("/title/" + movieId) == -1) {

                        String data = link + "," + sender + "|";
                        try {
                            data = data.replaceAll("[\n\r]", "");
                            appendData(fileLoc, data);

                            webpageData = redirectURL("http://www.imdbapi.com/?i=" + movieId + "&r=json");
                            String pageTitle = extImdbNameYear(webpageData);
                            sendMessage(channel, pageTitle + " added, Thanks!");
                        } catch (Exception e) {
                            sendMessage(channel, "An error occured while saving your recommendation :/");
                        }
                    } else {
                        sendMessage(channel, "The movie had been recommended before. Try another one.");
                    }

                } else {
                    sendMessage(channel, "Syntax error! Not an IMDB Link.");
                }

            } else {
                sendMessage(channel, "Syntax error! Type !man to see how to use the bot commands.");
            }

        }

    }

    //Function to append data to a file
    public void appendData(String fileLoc, String data) throws IOException {
        File file = new File(fileLoc);
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
        pw.println(data);
        pw.close();
    }

    //Function to override data to a file
    public void overrideData(String fileLoc, String data) throws IOException {
        File file = new File(fileLoc);
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        pw.println(data);
        pw.close();
    }

    //Methid to delete a recommendation
    public void deleteRecommendation(String message, String channel, String sender, String login, String hostname) {
        if (sender.equals(adminsUserName)) {

            if ((message.indexOf("http://www.imdb.com/title/") != -1) || (message.indexOf("https://www.imdb.com/title/") != -1)){
                String link = message.trim();
                link = message.substring(message.indexOf(" ") + 1);
                link = link.trim();
                int err = 0;

                String fileLoc = "./rec.dat";
                String fileData = "";
                try {
                    fileData = readEntireFile(fileLoc);
                    fileData = fileData.trim();
                } catch (Exception e) {
                    sendMessage(channel, "A problem occured while reading recommendations file. :/");
                    err = 1;
                }

                if (err == 0) {
                    int start = fileData.indexOf(link);
                    if (start != -1) {
                        String dataToRemove = fileData.substring(start, (fileData.indexOf("|", start + 1) + 1));
                        System.out.println("String Recommendation that the program is trying to remove: '" + dataToRemove + "'");
                        fileData = fileData.replaceAll(Pattern.quote(dataToRemove), "");
                        try {
                            fileData = fileData.replaceAll("[\n\r]", "");
                            overrideData(fileLoc, fileData);
                            sendMessage(channel, "Movie deleted!");
                        } catch (Exception e) {
                            sendMessage(channel, "An error occured while trying deleting the link.");
                        }
                    } else {
                        sendMessage(channel, "We dont have that movie as a recommendation.");
                    }

                }


            } else {
                sendMessage(channel, "Not an IMDB link.");
            }
        } else {
            sendMessage(channel, "You don't have the permission to remove this link.");
        }
    }

    //Function to show current system time
    public void showCurrentSysTime(String message, String channel, String sender, String login, String hostname) {
        String time = new java.util.Date().toString();

        sendMessage(channel, sender + ": The current time is: " + time);
    }

    //Function to google search some and gen Im feeling lucky link
    public void googleSearch(String message, String channel, String sender, String login, String hostname) {
        String pageTitle;
        String googleSearch = message.substring(2);
        googleSearch = googleSearch.trim();
        try {
            sendMessage(channel, "https://www.google.com/search?q=" + URLEncoder.encode(googleSearch, "ISO-8859-1"));
            String data = redirectURL("http://www.google.com/search?btnI=Im+Feeling+Lucky&q=" + URLEncoder.encode(googleSearch, "ISO-8859-1"));
            if(newUrl.indexOf("http://www.imdb.com/title/") != -1 || newUrl.indexOf("https://www.imdb.com/title/") != -1){
                String tempLink = newUrl;
                int start = newUrl.indexOf("tt", 3);
                String imdbId = newUrl.substring(start, start + 9);
                data = redirectURL("http://www.imdbapi.com/?i=" + imdbId + "&r=json");
                newUrl = tempLink;
                pageTitle = extImdbNameYear(data);
            }else{
                pageTitle = extTitleOfWebPage(data);
            }
            if (pageTitle == null || pageTitle.equals("") || pageTitle.equals(" ")) {
            } else {
                sendMessage(channel, Colors.BOLD + pageTitle + Colors.NORMAL + Colors.BOLD +":");
            }
            sendMessage(channel, newUrl);
        } catch (Exception e) {
            sendMessage(channel, "An Error occured. Please try again later :(");
        }
    }


    //Function to PM all the last msgs to the sender
    public void showLastMsgs(String sender){
        if(msgStake.size() > 0){
            for(String msg : msgStake){
                sendMessage(sender, msg);
            }
            sendMessage(sender, "Current system time:" + currentTime());
        } else {
            sendMessage(sender, "No new messages.");
        }

    }


    //Function to return current time in HH:mm:ss
    public String currentTime(){
        Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	return sdf.format(cal.getTime());
    }


    //Function to say Cool Story Bro
    public void coolStoryBro(String message, String channel, String sender, String login, String hostname) {
        String cool[] = {"Cool", "Nifty", "Neat"};
        String story[] = {"story", "tale", "anecdote"};
        String bro[] = {"bro", "homie", "amigo"};
        String finalOut;
        int tempInt;
        tempInt = randomBetweenRange(0, cool.length - 1);
        finalOut = cool[tempInt];
        tempInt = randomBetweenRange(0, story.length - 1);
        finalOut = finalOut + " " + story[tempInt];
        tempInt = randomBetweenRange(0, bro.length - 1);
        finalOut = finalOut + " " + bro[tempInt];
        sendMessage(channel, finalOut);
        tempInt = 0;
        finalOut = "";
    }

    //Function to find a random number
    public void randomNumber(String message, String channel, String sender, String login, String hostname) {
        String tempTokens = message.trim();
        String[] splited = tempTokens.split("\\s+");

        if (splited.length > 3) {
            sendMessage(channel, "Syntax error!");
        } else if (splited.length == 2 && isInteger(splited[1])) {

            sendMessage(channel, sender + " : " + randomNumExt(redirectURL("http://www.random.org/integers/?num=1&min=0&max=" + splited[1] + "&col=5&base=10&format=html&rnd=new")));
        } else if (splited.length == 3 && isInteger(splited[1]) && isInteger(splited[2]) && Integer.parseInt(splited[1]) < Integer.parseInt(splited[2])) {

            sendMessage(channel, sender + " : " + randomNumExt(redirectURL("http://www.random.org/integers/?num=1&min=" + splited[1] + "&max=" + splited[2] + "&col=5&base=10&format=html&rnd=new")));
        } else {
            sendMessage(channel, "Syntax error!");
        }
    }

    //Function to find random movie that matches users specified genere(if given). Note: Some generes are commented out because sometimes it can take a lot
    // of requests to the api server to find movies that are of niche genre and also have good rating.
    public void randomMovie(String message, String channel, String sender, String login, String hostname) {
        String genre = "";
        message = message.trim();
        String[] splited = message.split("\\s+");
        int notAGenreErr = 0;

        if (splited.length >= 2) {
            if (splited[1].equalsIgnoreCase("action")) {
                genre = "Action";
            } else if (splited[1].equalsIgnoreCase("Adventure")) {
                genre = "Adventure";
            } /*else if (splited[1].equalsIgnoreCase("Animation")) {
             genre = "Animation";
             } else if (splited[1].equalsIgnoreCase("Biography")) {
             genre = "Biography";
             } */ else if (splited[1].equalsIgnoreCase("Comedy")) {
                genre = "Comedy";
            } /*else if (splited[1].equalsIgnoreCase("Crime")) {
             genre = "Crime";
             } else if (splited[1].equalsIgnoreCase("Documentary")) {
             genre = "Documentary";
             } */ else if (splited[1].equalsIgnoreCase("Drama")) {
                genre = "Drama";
            } /*else if (splited[1].equalsIgnoreCase("Family")) {
             genre = "Family";
             } else if (splited[1].equalsIgnoreCase("Fantasy")) {
             genre = "Fantasy";
             } else if (splited[1].equalsIgnoreCase("History")) {
             genre = "History";
             } else if (splited[1].equalsIgnoreCase("Horror")) {
             genre = "Horror";
             } else if (splited[1].equalsIgnoreCase("Musical")) {
             genre = "Musical";
             } */ else if (splited[1].equalsIgnoreCase("Mystery")) {
                genre = "Mystery";
            } else if (splited[1].equalsIgnoreCase("Romance")) {
                genre = "Romance";
            } else if (splited[1].equalsIgnoreCase("Sci-Fi") || splited[1].equalsIgnoreCase("Sci.Fi")) {
                genre = "Sci-Fi";
            } /*else if (splited[1].equalsIgnoreCase("Sport")) {
             genre = "Sport";
             } else if (splited[1].equalsIgnoreCase("Short")) {
             genre = "Short";
             } */ else if (splited[1].equalsIgnoreCase("Thriller")) {
                genre = "Thriller";
            } /*else if (splited[1].equalsIgnoreCase("War")) {
             genre = "War";
             } else if (splited[1].equalsIgnoreCase("Western")) {
             genre = "Western";
             } */ else {
                notAGenreErr = 1;
            }
        }
        int count = 0;
        if (notAGenreErr == 0 || splited.length == 1) {
            while (1 == 1) {
                if(count > maxAPIrequest){
                  sendMessage(channel, sender + ": Ignoring your command because it was taking too long to find a movie with genre: " + genre);
                  System.out.println("IRC Bot Warning: Took more than " + maxAPIrequest + " requests. Ignoring command.");
                }
                String imdbLink, imdbId;
                imdbLink = randomImdbExt(redirectURL("http://www.imdb.com/random/title"));
                int start = imdbLink.indexOf("tt", 3);
                imdbId = imdbLink.substring(start, start + 9);
                sourceData = redirectURL("http://www.imdbapi.com/?i=" + imdbId + "&r=json");
                if (sourceData.indexOf(",\"Type\":\"movie\",") != -1) {
                    if (splited.length == 1) {
                        System.out.println("Showing a movie with a random genre");
                        sendMessage(channel, sender + ": " + Colors.BOLD + extImdbNameYear(sourceData) + Colors.NORMAL + " " + "http://www.imdb.com/title/" + imdbId);
                        break;
                    } else {
                        if (hasGenre(sourceData, genre)) {
                            System.out.println("Showing a movie with a genre of " + genre);
                            sendMessage(channel, sender + ": " + Colors.BOLD + extImdbNameYear(sourceData) + Colors.NORMAL + " " + "http://www.imdb.com/title/" + imdbId);
                            break;
                        }
                    }
                }
                count++;
            }
        } else {
            sendMessage(channel, sender + ": Syntax error!");
        }
    }
}
