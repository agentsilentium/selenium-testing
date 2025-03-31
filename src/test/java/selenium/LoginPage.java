package selenium;

import java.util.List;

import org.openqa.selenium.By;

public class LoginPage extends BasePage<LoginPage>{

    private static final String TITLE = "//h2[text()='Login Page']";
    private static final String SUB_HEADER = "//h4[@class='subheader']";
    private static final String USERNAME_INPUT = "//input[@id='username']";
    private static final String PWD_INPUT = "//input[@id='password']";
    private static final String LOGIN_BTN = "//button[@type='submit']";


    @Override
    public String getPageUrl() {
        return "/login";
    }

    @Override
    public LoginPage waitForPageInitialized() {
        List<String> fixElements = List.of(TITLE,SUB_HEADER,USERNAME_INPUT,PWD_INPUT,LOGIN_BTN);
        fixElements.forEach(locator -> waitForElementToBePresent(By.xpath(locator), 5));
        return self();
    }

    public LoginPage fillUsername(String username){
        return inputText(By.xpath(USERNAME_INPUT), username);
    }

    public LoginPage fillPassword(String password){
        return inputText(By.xpath(PWD_INPUT), password);
    }

    public SecureAreaPage clickLogin(){
        clickElement(By.xpath(LOGIN_BTN), 5);
        return new SecureAreaPage();
    }

}