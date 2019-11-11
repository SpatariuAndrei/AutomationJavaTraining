package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DynamicLoadingExample2Page {

    private WebDriver driver;
    @FindBy(id = "indicator")
    private WebElement loadingIndicator;
    @FindBy(css = "#start button")
    private WebElement startButton;
    @FindBy(id = "finish")
    private WebElement result;

    DynamicLoadingExample2Page(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void clickStart() {
        startButton.click();
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("finish")));
    }

    public String getResult() {
        return result.getText();
    }

    public boolean isStartButtonDisplayed() {
        return startButton.isDisplayed();
    }
}
