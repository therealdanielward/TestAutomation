package chrome.webdriver.hoopers;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

/*This Test case is very simple but will give you all errors
 * on the page currently on the time of load
 * and lists them into a database connection of your choosing
 * All is required is that you enter the details required for the test below
 * and for you to enter all clickable links*/

public class pageLoad
	{
		/*******************************************
		 * Details Required for Test
		 *******************************************/
		// CLient Log Table
		public static String logtable = "tbl_ErrorLog_Hoopers";
		// Website URL
		public static String siteUrl = "http://www.hoopers.co.za/";
		//Name for Runtimefile
		public static String runTimeName = "Hoopers";

		/*******************************************/

		// Location of Selenium webdriver.exe
		public static String seleniumWebdriverLocation = "C:\\chromedriver.exe";

		// Connect credentials to database
		String urldb = "ixsql.c29.co.za";
		String username = "Daniel";
		String password = "!MonkeyBalls$";
		String dbName = "Testers";

		/********************************************
		 * DECLARATIONS
		 ********************************************/

		// ArrayLists for storing data
		ArrayList<String> urlFromSitemap = new ArrayList<String>();
		ArrayList<String> timeStamp = new ArrayList<String>();
		ArrayList<String> url = new ArrayList<String>();
		ArrayList<String> statusLevel = new ArrayList<String>();
		ArrayList<String> errorMessage = new ArrayList<String>();

		// Webdriver for chrome
		WebDriver chrome;
		// Used to run Javascript funtions eg: Scrolling
		JavascriptExecutor jse;

		/*********************************************/
		
		// Connection for DB
		public Connection connectToDb()
			{

				Connection connection = null;
				String URLConnection;

				try
					{
						Class.forName("net.sourceforge.jtds.jdbc.Driver");
						URLConnection = "jdbc:jtds:sqlserver://" + urldb + ";" + dbName + ";user=" + username
								+ ";password=" + password + ";";
						connection = DriverManager.getConnection(URLConnection);
					}

				catch (ClassNotFoundException e)
					{
						e.printStackTrace();
					}

				catch (SQLException e)
					{
						e.printStackTrace();
					}

				return connection;
			}

		// Browser setup
		public void invokeBrowser()
			{
				try
					{
						System.setProperty("webdriver.chrome.driver", seleniumWebdriverLocation);
						chrome = new ChromeDriver();
						chrome.manage().deleteAllCookies();
						chrome.manage().window().maximize();
						chrome.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
						chrome.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);

						chrome.get(siteUrl + "/sitemap.xml");

					} catch (Exception e)
					{
						e.printStackTrace();
					}

			}

		// Checks your list created
		public void processArrayList()
			{
				//Get Start Time
				long startTime = System.currentTimeMillis();
				// Loop through the array of menu items
				try
					{
						
						//Loops until the end of the URLS
						for (int i = 0; i < urlFromSitemap.size(); i++)
							{
								chrome.get(urlFromSitemap.get(i));
								Thread.sleep(11000);

								// Logs the error entry from the browser console
								LogEntries le = chrome.manage().logs().get(LogType.BROWSER);
								
								//Loops through entries to get information and stores them in the ArrayLists
								for (LogEntry entry : le)
									{

										String tempTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(entry.getTimestamp()));
										Level tempLevel = entry.getLevel();
										String tempMessage = entry.getMessage();
										String firstReplace = tempMessage.replaceAll("\"", " ");
										String tempMessageFinal = firstReplace.replaceAll("'", " ");

										timeStamp.add(tempTime.toString());
										url.add(chrome.getCurrentUrl().toString());
										statusLevel.add(tempLevel.toString());
										errorMessage.add(tempMessageFinal);
									}
								chrome.navigate().back();
								Thread.sleep(2000);

							}
						
						//Time when task finished
						long endTime = System.currentTimeMillis();
						//Total runtime of task
						long totalRuntime = endTime - startTime;
						
						//Formats time 
						String timeFormatted = String.format("%d min, %d sec", 
							    TimeUnit.MILLISECONDS.toMinutes(totalRuntime),
							    TimeUnit.MILLISECONDS.toSeconds(totalRuntime) - 
							    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totalRuntime))
							);
						
						//get current date
						DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
						Date date = new Date();
						System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
						
						//Prints to a textfile in the folder the time taken
						PrintWriter writer = new PrintWriter(runTimeName+"_Runtime.txt", "UTF-8");
						writer.println("Date:");
						writer.println(date);
						writer.println("Time:");
						writer.println(timeFormatted);
						writer.close();
						
						closeBrowser();// calling of the closebrowser method

						// Connecting to DB and then and updating errors in tables
						Connection con = connectToDb();
						Statement s = con.createStatement();
						s.execute("DELETE FROM " + logtable + "");

						// Sending INSERT query to the database
						for (int i = 0; i < timeStamp.size(); i++)
							{
								String query = "INSERT INTO " + logtable
										+ "(Timestamp, URL, StatusLevel, ErrorMessage) VALUES ('" + timeStamp.get(i)
										+ "', '" + url.get(i) + "', '" + statusLevel.get(i) + "', '"
										+ errorMessage.get(i) + "'); ";
								s.executeUpdate(query);
							}

					} catch (Exception e)
					{
						e.printStackTrace();
					}

			}

		// Testing all links for sitemap
		public void getSitemapLinks()
			{

				// get all links from page
				List<WebElement> links = chrome.findElements(By.tagName("span"));
				for (WebElement link : links)
					{
						if (link.getText().contains(siteUrl))
							{
								urlFromSitemap.add(link.getText());
							}
					}
			}

		// Closes browser
		public void closeBrowser()

			{
				chrome.quit();
			}

		//Create Table
		public void createTable()
		{
			Connection createTableCon = connectToDb();
			//Trys to find table
			try {
			Statement s = createTableCon.createStatement();
			s.executeQuery("SELECT * FROM "+logtable);
			System.out.println("Table Found...");
			}
			//Catches an error if table doesnt exist then creates the table
			catch (Exception e)
			{
				Statement s;
				try
					{
						s = createTableCon.createStatement();
						s.execute("USE Testers Create table "+logtable+"(CompleteDate DateTime,URL varchar(MAX),StatusLevel varchar(MAX),ErrorMessage varchar(MAX))");
						System.out.println("Table Created...");
					} 
				//Catches a bad connection/ or bad query
				catch (SQLException e1)
					{
						System.out.println("Connection to database unavailable/or couldnt run query...");
						e1.printStackTrace();
					}
				
			}
		}
		
		// Main Method
		public static void main(String[] args)
			{

				pageLoad pl = new pageLoad();
				
				System.out.println("*Starting Error report for: "+runTimeName+
									"\n*With URL: "+siteUrl+
									"\n*AND Table: "+logtable+"\n");
				
				System.out.println("*Please ensure the chrome driver is setup correct, it is currently setup under:\n*"+seleniumWebdriverLocation);
				
				pl.createTable();		
										/*Creates a table if the table doesnt exist already*/
				
				//pl.invokeBrowser(); 
										/* Calls the method invokeBrowser() which sets up the setting for the browser */

				//pl.getSitemapLinks(); 
										/*
										 * Calling of the getSitemapLinks() method - Opens up the sitemap url and gets
										 * all the links from from the web elements for the processArrayList() method
										 */

				//pl.processArrayList(); 
										 /*
										 * Calling of the processArrayList() method - This runs through the list of
										 * urls, Uses Selenium to go to them in the browser and gets the browser log for
										 * that page then queries the database to update the rows with the errors
										 */
				System.out.println("*Completed Error Report for: "+runTimeName+"...\n\n");

			}

	}
