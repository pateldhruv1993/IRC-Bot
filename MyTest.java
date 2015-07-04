import java.net.MalformedURLException;
import java.security.cert.Certificate;
import java.io.*;
import java.io.*;
import java.net.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class MyTest{
  
  public static String newUrl;
  
  public static void main(String[] args)
  {
    new MyTest().redirectURL("http://www.random.org/integers/?num=1&min=0&max=10&col=5&base=10&format=html&rnd=new");
  }
  
  private void scrapeHTTPSLinks(String https_url){
    
    URL url;
    try {
      
      url = new URL(https_url);
      HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
      con.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
      con.addRequestProperty("User-Agent", "Mozilla");
      con.addRequestProperty("Referer", "google.com");
      
      //dumpl all cert info
      print_https_cert(con);
      
      //dump all the content
      print_content(con);
      
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
  
  
  //Method to handle url redirect and stuff. This method produces/changes sourceData variable with the source of the webpage
  public String redirectURL(String url) {
    try {
      URL obj = new URL(url);
      HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
      conn.setReadTimeout(2000);
      conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
      conn.addRequestProperty("User-Agent", "Mozilla");
      conn.addRequestProperty("Referer", "google.com");
      boolean redirect = false;
      // Normally, 3xx is redirect
      int status = conn.getResponseCode();
      if (status != HttpURLConnection.HTTP_OK) {
        if (status == HttpURLConnection.HTTP_MOVED_TEMP
              || status == HttpURLConnection.HTTP_MOVED_PERM
              || status == HttpURLConnection.HTTP_SEE_OTHER) {
          redirect = true;
        }
      }
      
      if (redirect) {
        
        // get redirect url from "location" header field
        newUrl = conn.getHeaderField("Location");
        
        System.out.println("The redirected URL" +newUrl);
        return "";
        
        /*
        
        // get the cookie if need, for login
        String cookies = conn.getHeaderField("Set-Cookie");
        
        // open the new connnection again
        conn = (HttpURLConnection) new URL(newUrl).openConnection();
        conn.setRequestProperty("Cookie", cookies);
        conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
        conn.addRequestProperty("User-Agent", "Mozilla");
        conn.addRequestProperty("Referer", "google.com");
        */
      }
      
      newUrl = conn.getURL().toString();
      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String inputLine;
      StringBuffer html = new StringBuffer();
      
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
  
  
  private void print_https_cert(HttpsURLConnection con){
    
    if(con!=null){
      
      try {
        
        System.out.println("Response Code : " + con.getResponseCode());
        System.out.println("Cipher Suite : " + con.getCipherSuite());
        System.out.println("\n");
        
        Certificate[] certs = con.getServerCertificates();
        for(Certificate cert : certs){
          System.out.println("Cert Type : " + cert.getType());
          System.out.println("Cert Hash Code : " + cert.hashCode());
          System.out.println("Cert Public Key Algorithm : " 
                               + cert.getPublicKey().getAlgorithm());
          System.out.println("Cert Public Key Format : " 
                               + cert.getPublicKey().getFormat());
          System.out.println("\n");
        }
        
      } catch (SSLPeerUnverifiedException e) {
        e.printStackTrace();
      } catch (IOException e){
        e.printStackTrace();
      }
      
    }
    
  }
  
  private void print_content(HttpsURLConnection con){
    if(con!=null){
      
      try {
        
        System.out.println("****** Content of the URL ********");   
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
        
        String input = "";
        while (br.readLine() != null){
          input += br.readLine();
          input += "\n";
        }
        br.close();
        
        System.out.println(input);
        
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}