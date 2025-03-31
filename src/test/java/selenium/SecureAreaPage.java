package selenium;

import java.util.List;

import org.openqa.selenium.By;

public class SecureAreaPage extends BasePage<SecureAreaPage>{

    private static final String TITLE = "//h2[contains(text(),'Secure Area')]";
    private static final String SUB_HEADER = "//h4[@class='subheader']";
    private static final String LOGOUT_BTN = "//a[@href='/logout']";

    @Override
    public String getPageUrl() {
        return "/secure";
    }

    @Override
    public SecureAreaPage waitForPageInitialized() {
        List<String> fixElements = List.of(TITLE,SUB_HEADER,LOGOUT_BTN);
        fixElements.forEach(locator -> waitForElementToBePresent(By.xpath(locator), 5));
        return self();
    }

    public LoginPage clickLogout(){
        clickElement(By.xpath(LOGOUT_BTN), 5);
        return new LoginPage();
    }

}
