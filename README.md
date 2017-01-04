<h1>What is this?</h1>
<p>
  This is a simple IRC bot that I made a year or so ago. It has some functionality that me and my friends on the IRC wanted. At some places the URLs that I use have been replaced with fake URLs to because of privacy reasons.
</p>
<b>NOTE: This bot does NOT handle modding of the channel. For example kicking people for spamming, using bigoted words etc. But can be easily added on top of this using the PIRC framework that is included.</b>

<h1>What can it do?</h1>
<ul>
  <li><b>!randomMovie [genre]  -</b> Finds a random movie using imdb.com/random/title and output's the IMDB link for that particular movie. The genre parameter is optional. Currently supported genres [Action, Adventure, Comedy, Drama, Mystery, Romance, Sci-Fi, Thriller].<br>
  <b>NOTE:</b> As the API does not have any command to directly give a result with specified genre, it will keep making requests till it find a movie with specified genre. To stop it from going on and on there's a "Max requests" parameter (40 by default). That is also the reason some of the genres are commented out in the code but you can go ballsy and give your users the option to find a movie of any niche genre, it will just take more requests.</li>


  <li><b>!num &lt;A&gt; [B] -</b> Finds a random number using random.org . If only 1 parameter is given, the random number will be generated between 0 and <A>. Parameter [B] is optional. If [B] is given, the random number will be between A and B. The limit is  1,000,000,000</li>


  <li><b>!rec [link] -</b> If no link is given as a parameter, it outputs one of the random recommendations by the users. If a link is given, it will add that link to the database (rec.dat file) with the users name. ONLY IMDB LINKS ALLOWED. Format (http://www.imdb.com/title/tt0092115/ OR https://www.imdb.com/title/tt0092115/)</li>


  <li><b>!g -</b> Google something from irc! This command will output a google search link, a link to the first page from the results and the title of the said first page.</li>


  <li><b>!rules -</b> Will show predefined links to some important rule or channel.</li>


  <li><b>!sup -</b> Shows you the last 50 (can be changed to whatever you want) messages from the channel.</li>


  <li><b>!time -</b> Shows current time of BOT's system.</li>

  <li><b>!delrec - SPECIAL COMMAND</b>. Used to delete a troll recommendation. It can be only used by the user defined in "adminsUserName" parameter of the program.</li>

</ul>



<h1>Setup & Installation</h1>

<p> First open the "MyBotMain.java" file in any text editor and change the bot's connection settings. You can change the bots name, the irc server it connects to and the channel it will join after connecting. You can make the bot join multiple servers, channels all with different names if you want.</p>

<p>Then open up MyBot.java if you want to change command specific settings. For example (maxium requests to the API, max messages to store and output and admins username)</p>

<p>If you have JDK installed than all you need to do it open the "Run the IRC Bot.bat" file in a text editor and change the "set path" value to where ever the bin folder of your JDK installation is and than just double click it to run after you've saved your changes. <b>NOTE:</b> Make sure to keep the BAT file in the same folder as all the other files.</p>

<p>If you DONT have JDK installed than you need to go and download and install JDK first and then follow the above step.</p>


<h2>Extra</h2>
You can change the verbos to false in MyBotMain.java if you don't want the console to out put framework related output.

You can also comment out all the lines that start with System.out.println to turn off bot related debug output.
