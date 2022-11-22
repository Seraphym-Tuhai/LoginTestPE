import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

import static org.testng.Assert.fail;

public class LoginTest {
    private WebDriver driver;
    private WebDriverWait wait;

    private static final String EMAIL_FIELD = "//input[@id='username-input']";
    private static final String PASSWORD_FIELD = "//input[@id='password-input']";
    private static final String SUBMIT_BTN = "//button[@type='submit']";
    private static final String WELCOME_DIALOG_CLOSE_BTN = "//tb-welcome-dialog//span[contains(text(),'Close')]/..";
    private static final String SOLUTION_TEMPLATES_BTN = "//mat-toolbar//a[@href='/solutionTemplates']";
    private static final String SOLUTION_CARD = "//tb-solution-template-card";
    private static final String EMAIL = "stuhai@thingsboard.io";
    private static final String PASSWORD = "password123TB";
    private static final String URL = "https://thingsboard.cloud/login";

    private WebElement waitUntilElementToBeClickable(String locator) {
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(By.xpath(locator)));
        } catch (WebDriverException e) {
            fail("No clickable element: " + locator);
            return null;
        }
    }

    protected List<WebElement> waitUntilVisibilityOfElementsLocated(String locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(locator)));
        } catch (WebDriverException e) {
            return null;
        }
    }

    private boolean urlContains(String urlPath) {
        try {
            wait.until(ExpectedConditions.urlContains(urlPath));
        } catch (WebDriverException e) {
            fail("This URL path is missing");
        }
        return driver.getCurrentUrl().contains(urlPath);
    }

    @BeforeMethod
    public void openBrowser(){
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofMillis(5000));
        driver.manage().window().maximize();
        driver.get(URL);
    }

    @AfterMethod
    public void teardown(){
        driver.quit();
    }

    @Test
    public void loginTest() {
        waitUntilElementToBeClickable(EMAIL_FIELD).sendKeys(EMAIL);
        waitUntilElementToBeClickable(PASSWORD_FIELD).sendKeys(PASSWORD);
        waitUntilElementToBeClickable(SUBMIT_BTN).click();
        boolean urlContainsHome = urlContains("/home");
        waitUntilElementToBeClickable(WELCOME_DIALOG_CLOSE_BTN).click();
        waitUntilElementToBeClickable(SOLUTION_TEMPLATES_BTN).click();

        Assert.assertTrue(urlContainsHome);
        Assert.assertTrue(urlContains("solutionTemplates"));
        Assert.assertNotNull(waitUntilVisibilityOfElementsLocated(SOLUTION_CARD));
        waitUntilVisibilityOfElementsLocated(SOLUTION_CARD).forEach(x -> Assert.assertTrue(x.isDisplayed()));
    }
}