package selenium.webdriver.basic;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.JavascriptExecutor;

public class dayOne 
{
	
	WebDriver driver;
	JavascriptExecutor jse;
	
	public void invokeBrowser()
	{
		try 
		{
		System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(30,TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		
		driver.get("http://www.edureka.co");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void searchCourse()
	{
		try 
		{
			driver.findElement(By.id("homeSearchBar")).sendKeys("Java");
			driver.findElement(By.id("homeSearchBarIcon")).click();

			jse = (JavascriptExecutor)driver;
			jse.executeScript("scroll(0, 500)");

			Thread.sleep(6000);
			driver.findElement(By.xpath("//*[@id=\"categorylistmain\"]/div[2]/div[2]/ul/li[1]/label")).click();
			//label[//*contains(text(),"Weekend")]
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		 

	}
	
	public static void main(String[] args)
	{
		dayOne doo = new dayOne();
		doo.invokeBrowser();
		
		doo.searchCourse();
	}

}
