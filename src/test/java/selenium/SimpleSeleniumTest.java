package selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Attachment;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.qameta.allure.testng.AllureTestNg;

import org.assertj.core.api.Assertions;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Feature("Google Search")
@Listeners({AllureTestNg.class})
public class SimpleSeleniumTest {
    private WebDriver driver;

    @BeforeClass
    public void setUp() {
        // add "old" solution for comparison
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-infobars");
        driver = new EdgeDriver(options);
    }

    @Test
    @Story("Check Google Title")
    @Description("Verifies that Google homepage title is correct.")
    public void testGoogleTitle() {
        driver.get("https://www.google.com");
        String title = driver.getTitle();
        Assertions.assertThat(title).contains("Google");
    }

    @Test
    @Story("Search in Google")
    @Description("Searches for 'Selenium WebDriver' and checks the title.")
    public void testSearch() {
        driver.get("https://www.google.com");
        WebElement rejecElement = driver.findElement(By.xpath("(//button)[4]"));
        rejecElement.click();
        WebElement searchBox = driver.findElement(By.name("q"));
        takeScreenshot(driver);
        searchBox.click();
        searchBox.sendKeys("Selenium WebDriver");
        searchBox.submit();
        Assertions.assertThat(driver.getTitle()).contains("Selenium");
        takeScreenshot(driver);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Attachment(value = "Screenshot", type = "image/png")
    @Step("Take screenshot")
    public byte[] takeScreenshot(WebDriver driver) {
        // Take a screenshot and return the byte array
        TakesScreenshot ts = (TakesScreenshot) driver;
        byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
        return screenshot;
    }

     @AfterMethod
    public void captureScreenshotOnFailure(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            takeScreenshot(driver);  // Attach screenshot only if the test fails
        }
    }
}
