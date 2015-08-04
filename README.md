<h1>Whats this?</h1>
<p>
  This is a simple IRC bot that I made a year or so ago. It has some functionality that me and my friends on the IRC wanted.
</p>
<b>NOTE: This bot does NOT handle modding of the channel. For example kicking people for spamming, using bigoted words etc. But can be easily added on top of this using the PIRC framework that is included.</b>

<h1>What can it do?</h1>
<ul>
<li>!randomMovie [genre]  - Finds a random movie using imdb.com/random/title and output's the IMDB link for that particular movie. The genre parameter is optional. Currently supported genres -> [Action, Adventure, Comedy, Drama, Mystery, Romance, Sci-Fi, Thriller].
<b>NOTE:</b> As the API does not have any command to directly give a result with specified genre, it will keep making requests till it find a movie with specified genre. To stop it from going on and on there's a "Max requests" parameter (40 by default). That is also the reason some of the genres are commented out in the code but you can go ballsy and give your users the option to find a movie of any niche genre, it will just take more requests.</li>


<li>!num <A> [B] - Finds a random number using random.org . If only 1 parameter is given, the random number will be generated between 0 and <A>. Parameter [B] is optional. If [B] is given, the random number will be between A and B. The limit is  1,000,000,000</li>


<li>!rec [link] - If no link is given as a parameter, it outputs one of the random recommendations by the users. If a link is given, it will add that link to the database with the users name. ONLY IMDB LINKS ALLOWED. Format (http://www.imdb.com/title/tt0092115/ OR https://www.imdb.com/title/tt0092115/)</li>


<li>!g - Google something from irc! This command will output a google search link, a link to the first page from the results and the title of the said first page.</li>


<li>!rules - Will show predefined links to some important rule or channel.</li>


<li>!sup - Shows you the last 50 (can be changed to whatever you want) messages from the channel.</li>


<li>!time - Shows current time of BOT's system.</li>

<li>!delrec - SPECIAL COMMAND. Used to delete a troll recommendation. It can be only used by the user defined in adminsUserName parameter of the program.</li>

</ul>
