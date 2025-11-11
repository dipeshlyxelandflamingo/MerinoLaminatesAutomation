package com.Merino;

import java.time.Duration;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Grande_Collection {

	WebDriver driver;
    WebDriverWait wait;

    @BeforeTest
    public void OpenBrowser() throws InterruptedException {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // ✅ Use realistic browser fingerprint to bypass Cloudflare
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("start-maximized");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/142.0.7444.134 Safari/537.36");

        // ✅ Headless + Jenkins safe setup
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
        }

        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        driver.get("https://www.merinolaminates.com/en/product-category/grande-collection");

        // 🩵 Cloudflare detection and retry
        String title = driver.getTitle();
        if (title.contains("Attention Required") || title.contains("Cloudflare")) {
            System.out.println("⚠ Cloudflare challenge detected. Retrying after 5 seconds...");
            Thread.sleep(5000);
            driver.navigate().refresh();
        }

        // Close notification if visible
        try {
            WebElement cancel = wait.until(
                    ExpectedConditions.elementToBeClickable(By.id("onesignal-slidedown-cancel-button")));
            cancel.click();
        } catch (Exception e) {
            System.out.println("Notification popup not displayed.");
        }
    }

    @Test(priority = 1)
    public void TC_01_Verify_GrandeCollectionPageTitle() {
        String expectedTitle = "Grande Collection by Merino Laminates – 10ft Designer Laminates";
        String actualTitle = driver.getTitle();

        // Normalize dashes
        expectedTitle = expectedTitle.replaceAll("[–—−]", "-").trim();
        actualTitle = actualTitle.replaceAll("[–—−]", "-").trim();

        System.out.println("Actual Title: " + actualTitle);
        Assert.assertEquals(actualTitle, expectedTitle, "❌ Page title mismatch!");
        System.out.println("✅ Page title verified successfully.");
    }

    @Test(priority = 2)
    public void TC_02_FormSubmission() throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Scroll to form
        js.executeScript("window.scrollBy(0,5300)");
        Thread.sleep(4000);

        // Fill the form safely
        driver.findElement(By.name("Name")).sendKeys("Dipesh");
        driver.findElement(By.name("email")).sendKeys("dipesh123@yopmail.com");
        driver.findElement(By.name("mobile")).sendKeys("6354899390");

        new Select(driver.findElement(By.name("Country"))).selectByVisibleText("India");
        Thread.sleep(2000);      
        new Select(driver.findElement(By.id("stateDropDown"))).selectByVisibleText("Gujarat");
        Thread.sleep(2000); 
        new Select(driver.findElement(By.xpath("//select[@name='city']"))).selectByIndex(1);
        Thread.sleep(4000); 
        new Select(driver.findElement(By.name("you_are"))).selectByVisibleText("OEMs");
        Thread.sleep(3000); 

        driver.findElement(By.name("age_confirm")).click();
        Thread.sleep(3000); 
        // Wait for submit button and click
        WebElement submitBtn = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector("input[value='Submit']")));
        js.executeScript("arguments[0].scrollIntoView(true);", submitBtn);
        js.executeScript("arguments[0].click();", submitBtn);

        // Wait for message response
        Thread.sleep(4000);

        try {
            WebElement successMsg = driver
                    .findElement(By.xpath("//div[contains(text(),'Thank you for your message')]"));
            Assert.assertTrue(successMsg.isDisplayed(), "Success message not visible!");
            System.out.println("✅ Form submitted successfully — Thank You message displayed.");
        } catch (NoSuchElementException e) {
            try {
                WebElement errorMsg = driver.findElement(
                        By.xpath("//div[contains(text(),'There was an error trying to send your message')]"));
                String msg = errorMsg.getText();
                Assert.fail("❌ Form submission failed! Error message: " + msg);
            } catch (Exception ex) {
                Assert.fail("❌ Form submission failed! No confirmation message found.");
            }
        }
    }

    @AfterTest
    public void TearDown() {
        if (driver != null) {
            driver.quit();
            System.out.println("🔒 Browser closed successfully.");
        }
    }
}