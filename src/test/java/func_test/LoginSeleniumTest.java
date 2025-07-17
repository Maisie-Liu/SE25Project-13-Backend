import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertTrue;

import java.time.Duration;

public class LoginSeleniumTest {
    private WebDriver driver;

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "F:/chromedriver/chromedriver-win64/chromedriver.exe");
        System.setProperty("webdriver.http.factory", "jdk-http-client");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless=new");
        options.addArguments("--disable-web-security"); // 禁用同源策略
        options.addArguments("--allow-running-insecure-content"); // 允许不安全内容
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    public void testLogin() {
        System.out.println("准备访问登录页...");
        driver.get("http://localhost:3000/login");
        System.out.println("已访问登录页，准备查找元素...");
        WebElement usernameInput = driver.findElement(By.name("username"));
        usernameInput.sendKeys("user1");
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys("123456");
        WebElement loginBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        loginBtn.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/"));
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.equals("http://localhost:3000/"));
    }

    @Test
    public void testLoginWithNonexistentUser() {
        System.out.println("测试不存在的用户名...");
        driver.get("http://localhost:3000/login");
        WebElement usernameInput = driver.findElement(By.name("username"));
        usernameInput.sendKeys("notexistuser");
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys("anyPassword");
        WebElement loginBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        loginBtn.click();
        // 等待错误提示出现
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-alert-description")));
        String alertText = alert.getText();
        System.out.println("错误提示: " + alertText);
        assertTrue(alertText.contains("用户不存在") || alertText.contains("Bad credentials") || alertText.contains("登录失败"));
    }

    @Test
    public void testLoginWithWrongPassword() {
        System.out.println("测试密码错误...");
        driver.get("http://localhost:3000/login");
        WebElement usernameInput = driver.findElement(By.name("username"));
        usernameInput.sendKeys("user1");
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys("wrongpassword");
        WebElement loginBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        loginBtn.click();
        // 等待错误提示出现
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-alert-description")));
        String alertText = alert.getText();
        System.out.println("错误提示: " + alertText);
        assertTrue(alertText.contains("密码") || alertText.contains("Bad credentials") || alertText.contains("登录失败"));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
} 