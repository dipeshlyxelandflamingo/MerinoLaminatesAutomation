package com.Merino;

import java.time.Duration;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Grande_Collection {

	WebDriver driver;

	@BeforeTest
	public void OpenBrowser() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.get("https://www.merinolaminates.com/en/product-category/grande-collection");
		driver.manage().window().maximize();

		// Close notification if visible

		driver.findElement(By.id("onesignal-slidedown-cancel-button")).click();

	}

	@Test
	public void TC_01_PageTitle() {

		// Verify page title
		String expectedTitle = "Grande Collection by Merino Laminates – 10ft Designer Laminates";
		String actualTitle = driver.getTitle();
		Assert.assertEquals(actualTitle, expectedTitle, "Page title mismatch!");

	}

	@Test
	public void TC_02_FormValidation() throws InterruptedException {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,5300)");

		// Fill form
		driver.findElement(By.xpath("//input[@name='Name']")).sendKeys("Dipesh");
		driver.findElement(By.xpath("//input[@name='email']")).sendKeys("dipesh@yopmail.com");
		driver.findElement(By.xpath("//input[@name='mobile']")).sendKeys("6354899390");
		
		Select country = new Select(driver.findElement(By.xpath("//select[@name='Country']")));
		country.selectByVisibleText("India");

		Select state = new Select(driver.findElement(By.id("stateDropDown")));
		state.selectByVisibleText("Uttar Pradesh");

		Select city = new Select(driver.findElement(By.xpath("//select[@name='city']")));
		city.selectByVisibleText("Gautam Buddha Nagar");

		Select youare = new Select(driver.findElement(By.xpath("//select[@name='you_are']")));
		youare.selectByVisibleText("Interior Designer");

		driver.findElement(By.xpath("//input[@name='Query']")).sendKeys("Query");
		driver.findElement(By.xpath("//input[@name='age_confirm']")).click();
		
		Thread.sleep(5000);
		//driver.findElement(By.xpath("//input[@value='Submit']")).click();

		Thread.sleep(3000); // wait for response

		try {
			WebElement successMsg = driver
					.findElement(By.xpath("//div[text()='Thank you for your message. It has been sent.']"));
			Assert.assertTrue(successMsg.isDisplayed(), "Form submitted successfully!");
			System.out.println("✅ Form submitted successfully, Thank You message displayed.");
		} catch (NoSuchElementException e) {
			// Error message
			WebElement errorMsg = driver.findElement(By
					.xpath("//div[text()= 'There was an error trying to send your message. Please try again later.']"));
			String msg = errorMsg.getText();
			Assert.fail("❌ Form submission failed! Error message: " + msg);
		}
	}
	
	
	@AfterTest(enabled=false)
	public void TearDown() {
		
		driver.quit();
		
	}
	
}