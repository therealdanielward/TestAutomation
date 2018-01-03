package run.all.clients;

import chrome.webdriver.console.error.log.pageLoadCore;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class runAllClients
  {

//ArrayLists<String> to hold the URLS and ClientNames
public static ArrayList<String> clientName = new ArrayList<String>();
public static ArrayList<String> URL = new ArrayList<String>();

public static ResultSet r;

public static void main(String[] args) throws IOException
  {

    FileWriter fw = new FileWriter("ConsoleLog.txt", true);
    BufferedWriter bw = new BufferedWriter(fw);
    PrintWriter out = new PrintWriter(bw);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    // IMPORTANT: Save the old System.out!
    PrintStream old = System.out;
    // Tell Java to use your special stream
    System.setOut(ps);

    //Calling the pageLoadCore project to run the website names and URLs
    pageLoadCore plc = new pageLoadCore();
    Connection c = plc.connectToDb();
    Statement s;

    //The Query to get the URLs and The ClientNames
    try
      {
        s = c.createStatement();
        r = s.executeQuery("USE Dealergateway SELECT [acuClientID], [acuURL], B.clientName FROM [AutoEngine].[dbo].[tbl_AdminClientURL] A, [AutoEngine].[dbo].[tbl_AdminClient] B WHERE acuPriority = 1 AND acuClientID IN (SELECT DISTINCT[pageClient] FROM [WebsiteEditor].[dbo].[tbl_WE_ContentPage] where pageDeleteDate IS NULL) AND acuID IN (SELECT acuId FROM (SELECT DISTINCT [acuClientID], min(acuId) AS acuID FROM [AutoEngine].[dbo].[tbl_AdminClientURL] WHERE acuPriority = 1 GROUP BY [acuClientID]) u) AND A.acuClientID = B.clientID ORDER BY acuClientid");

      } catch (SQLException ex)
      {
        ex.printStackTrace();
      }

    //Runs through the ResultSet to get the ClientName and URL and put it in the ArrayLists<String>
    try
      {
        for (int i = 0; r.next(); i++)
          {
            clientName.add(r.getString("clientName").replaceAll(" ", "").replaceAll("-", "_").replaceAll("&", "And").replaceAll("/", "").replaceAll(".co.za", "").replaceAll(" ", "").replaceAll("[()]", ""));
            // Added a Replaceall for blank spaces for when it creates the table names
            URL.add(r.getString("acuURL").replaceAll("www.", ""));
          }
      } catch (SQLException ex)
      {

        ex.printStackTrace();
      }

    //For loop to run through all the URLs and adds them to the pageLoadCore to run through
    for (int i = 200; i <= URL.size(); i++)//Can change the integer starting increment to do different clients
      {
        System.out.println("***********" + clientName.get(i) + "************");
        System.out.println("Client number " + (i + 1) + " of " + URL.size());
        String url = URL.get(i);
        String client = clientName.get(i);
        plc.setURL("http://" + url);
        plc.setClient(client);
        plc.invokeBrowser();
        plc.getSitemapLinks();
        plc.processArrayList();

        System.out.flush();
        System.setOut(old);

        out.println(baos.toString());
        out.close();
      }

  }

  }
