package selenium;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.testng.Assert;

import io.github.bonigarcia.wdm.WebDriverManager;

public abstract class BasePage<T extends BasePage<T>> {

    protected Logger LOGGER;
    protected static WebDriver webDriver;

    protected BasePage() {
        initWebDriver();
        LOGGER = Logger.getLogger(getPageUrl());
    }

    WebDriver getWebDriver() {
        initWebDriver();
        return webDriver;
    }

    private static void initWebDriver() {
        if (webDriver == null) {
            WebDriverManager.edgedriver().setup();
            webDriver = new EdgeDriver();
        }
    }

    protected T goToPage() {
        String finalUrl = "https://the-internet.herokuapp.com" + getPageUrl();
        webDriver.get(finalUrl);
        waitForPageInitialized();
        return self();
    }

    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    public abstract String getPageUrl();

    public abstract T waitForPageInitialized();

    public T screenShotOnStep() {
        attachScreenshot();
        return self();
    }

    protected void attachScreenshot() {
        File screenshot = null;
        int retry = 3;
        while (retry-- > 0 && screenshot == null) {
            try {
                screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
            } catch (TimeoutException e) {
                LOGGER.warning("could not take screenshot yet");
            }
            if (screenshot == null) {
                // log
                return;
            }
            String timeStamp = new SimpleDateFormat("MMddHHmmssSS").format(new Date());
            String currentDir = System.getProperty("user.dir");
            File attachedScreenshot = new File(currentDir + "/screenshots/" + timeStamp + ".png");
            try {
                FileUtils.copyFile(screenshot, attachedScreenshot);
                attachedScreenshot.setReadable(true, false);
                attachedScreenshot.setWritable(true, false);
            } catch (IOException e) {
                LOGGER.info("what");
            }
        }
    }

    protected T waitUntilLoadingIsFinished() {
        return waitUntilLoadingIsFinished(10);
    }

    protected T waitUntilLoadingIsFinished(int timeout) {
        waitForCondition(
                ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("loading"))));
        waitForCondition(
                ExpectedConditions.not(
                        ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("loading_css"))));
        return self();
    }

    public T waitForElementToBePresent(By elementLocator, int timeout) {
        waitUntilLoadingIsFinished();
        waitForCondition(ExpectedConditions.presenceOfElementLocated(elementLocator));
        waitForCondition(ExpectedConditions.visibilityOfElementLocated(elementLocator));
        return self();
    }

    public T waitForElementNotPresent(String elementXpath, int timeout) {
        waitForCondition(
                ExpectedConditions.not(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(elementXpath))));
        return self();
    }

    public T waitForElementToBeClickable(By elementLocator) {
        waitUntilLoadingIsFinished();
        waitForCondition(ExpectedConditions.elementToBeClickable(elementLocator));
        return self();
    }

    public T scrollElementIntoView(By elementLocator, boolean retry) {
        try {
            WebElement element = findElement(elementLocator);
            Point point = element.getLocation();
            JavascriptExecutor js = (JavascriptExecutor) webDriver;
            js.executeScript("window.scrollTo(0, " + point.getY() + ");");
        } catch (StaleElementReferenceException staleElementReferenceException) {
            if (retry) {
                LOGGER.warning("executeScript caught StaleElementReferenceException, try once more");
                scrollElementIntoView(elementLocator, false);
            } else {
                throw staleElementReferenceException;
            }
        } catch (WebDriverException | NullPointerException e) {
            attachScreenshot();
            throw e;
        }
        return self();
    }

    public T inputText(By elementLocator, String textToInput) {
        waitForElementToBePresent(elementLocator, 10);
        waitForElementToBeClickable(elementLocator);
        try {
            webDriver.findElement(elementLocator).clear();
            webDriver.findElement(elementLocator).sendKeys(textToInput);
        } catch (WebDriverException e) {
            attachScreenshot();
            throw e;
        }
        return self();
    }

    public T clickElement(By elementLocator, int timeout) {
        waitUntilLoadingIsFinished();
        waitForElementToBePresent(elementLocator, timeout);
        waitForElementToBeClickable(elementLocator);
        try {
            // scroll & use action so that frontend logout timer resets
            scrollElementIntoView(elementLocator, true);
            new Actions(webDriver)
                    .moveToElement(webDriver.findElement(elementLocator))
                    .click()
                    .build()
                    .perform();
        } catch (MoveTargetOutOfBoundsException outOfBoundsException) {
            // in this case, the element is in a separate scrollable parent
            LOGGER.info(String.format("Could not scroll to %s, click through DOM", elementLocator));
            webDriver.findElement(elementLocator).click();
        } catch (StaleElementReferenceException staleException) {
            // retry, as we only reach this point if the element is found and present
            LOGGER.info("element is stale, retry click");
            return clickElement(elementLocator, timeout);
        } catch (WebDriverException e) {
            attachScreenshot();
            throw e;
        }
        waitUntilLoadingIsFinished(timeout);
        return self();
    }

    private <U> void waitForCondition(ExpectedCondition<U> condition) {
        waitForCondition(condition, 10);
    }

    private <U> void waitForCondition(ExpectedCondition<U> condition, int customTimeout) {
        try {
            new FluentWait<>(webDriver)
                    .withMessage(condition.toString() + " not met")
                    .withTimeout(Duration.ofSeconds(customTimeout))
                    .pollingEvery(Duration.ofSeconds(2)).ignoring(WebDriverException.class).until(condition);
        } catch (TimeoutException timeOutException) {
            attachScreenshot();
            Assert.fail(condition + " not met");
        }
    }

    private WebElement findElement(By elementLocator) {
        List<WebElement> foundElements = findElements(elementLocator);
        return foundElements.stream().findFirst().orElse(null);
    }

    private List<WebElement> findElements(By elementLocator) {
        List<WebElement> webElements = null;
        int i = 0;
        do {
            i++;
            try {
                webElements = new ArrayList<>(webDriver.findElements(elementLocator));
            } catch (WebDriverException e) {
                // log and sleep
            }
        } while (webElements == null && i != 3);
        if (webElements == null)
            attachScreenshot();
        return webElements;
    }

}