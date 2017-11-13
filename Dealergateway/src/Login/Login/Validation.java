package Login;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Validation 
{
	
	WebDriver chrome;
	JavascriptExecutor jse;
	
	
	// Browser setup
	public void invokeBrowser()
		{
			try 
			{
			System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
			chrome = new ChromeDriver();
			chrome.manage().deleteAllCookies();
			chrome.manage().window().maximize();
			chrome.manage().timeouts().implicitlyWait(30,TimeUnit.SECONDS);
			chrome.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			
			chrome.get("http://qe.co.za");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	
	//Checks the validations with the wrong username and password input
	public void checkValidation()
	{
		try
			{
		chrome.findElement(By.id("loginPlaceholder_UserName")).sendKeys("test");
		chrome.findElement(By.id("loginPlaceholder_Password")).sendKeys("test");
		chrome.findElement(By.id("loginPlaceholder_LoginButton")).click();
		Thread.sleep(3000);
		
		if(chrome.getPageSource().contains("Your login attempt was not successful. Please try again.")) 
			{
				System.out.println("Success!");
			}
		else
			{
				System.out.println("Does not contain the Validation text");
			}
		
		
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		
	}

	public static void main(String[] args)
	{
		//Initializing web driver
		Validation v = new Validation();
		v.invokeBrowser();
		
		//Checking of validation of username and password
		v.checkValidation();
	}
	
	
}
