package selenium;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class FirstClass {

    @Test
    public void adyashanti(){
        // to avoid having to download drivers
        WebDriverManager.edgedriver().setup();
        WebDriver driver = new EdgeDriver();

        //maximize window, and go to "https://terebess.hu/zen/

        // find and click on link with text 'Masters and Disciples'

        // get page title, and check if it contains 'Zen Masters and Disciples' via TestNG Assert

        // find and click on link with text 'Zen Masters'

        // get page title, and check if it contains 'Zen Masters' via TestNG Assert

        // find and click on link with text Adyashanti'

        // get page title, and check if it contains 'Adyashanti' via TestNG Assert

        /* check if the page contains the picture of Adyashanti, with the following details:
           it's source contains 'Adyashanti.jpg'
           it's dimensions are:
                width: 412
                height: 273
        */

        // find the meaning of his name on the page, and print it to the console

        // make sure the driver is closed properly at the end of test
    }
}
