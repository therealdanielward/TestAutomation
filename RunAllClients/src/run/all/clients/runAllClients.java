package run.all.clients;

import chrome.webdriver.console.error.log.pageLoadCore;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class runAllClients
 {

//ArrayLists<String> to hold the URLS and ClientNames
public static ArrayList<String> clientName = new ArrayList<String>();
public static ArrayList<String> URL = new ArrayList<String>();

public static ResultSet r;

public static void main(String[] args)
 {
	//Calling the pageLoadCore project to run the website names and URLs
	pageLoadCore plc = new pageLoadCore();
	Connection c = plc.connectToDb();
	Statement s;

	//The Query to get the URLs and The ClientNames
	try
	 {
		s = c.createStatement();
		r = s.executeQuery("Use Dealergateway SELECT [acuClientID], [acuURL], B.clientName FROM [AutoEngine].[dbo].[tbl_AdminClientURL] A,[AutoEngine].[dbo].[tbl_AdminClient] B where acuPriority = 1 and acuID in (select acuId from (SELECT distinct [acuClientID],min(acuId) as acuID FROM [AutoEngine].[dbo].[tbl_AdminClientURL] where acuPriority = 1 group by [acuClientID] ) u) AND A.acuClientID = B.clientID order by acuClientid");

	 } catch (SQLException ex)
	 {
		ex.printStackTrace();
	 }

	//Runs through the ResultSet to get the ClientName and URL and put it in the ArrayLists<String>
	try
	 {
		for (int i = 0; r.next(); i++)
		 {
			clientName.add(r.getString("clientName").replaceAll(" ", "").replaceAll("-", "_").replaceAll("&", "And"));
			// Added a Replaceall for blank spaces for when it creates the table names
			URL.add(r.getString("acuURL").replaceAll("www.", ""));
		 }
	 } catch (SQLException ex)
	 {
		ex.printStackTrace();
	 }

	//For loop to run through all the URLs and adds them to the pageLoadCore to run through
	for (int i = 0; i <= URL.size(); i++)//Can change the integer starting increment to do different clients
	 {
                        System.out.println("Client number "+(i+1)+" of "+URL.size());
		String url = URL.get(i);
		String client = clientName.get(i);

		plc.setURL("http://" + url);
		plc.setClient(client);
		plc.invokeBrowser();
		plc.getSitemapLinks();
                                    plc.createTable();
		plc.processArrayList();
	 }
 }

 }
