import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class RegisterSeleniumTest {
    private WebDriver driver;

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "F:/chromedriver/chromedriver-win64/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--headless=new");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    // 工具方法：生成唯一用户名
    private String uniqueUsername() {
        return "testuser" + UUID.randomUUID().toString().substring(0, 8);
    }

    // 工具方法：注册用户
    private void register(String username, String password, String confirmPassword, String nickname, String email, String phone) {
        driver.get("http://localhost:3000/register");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='username']"))).sendKeys(username);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='password']"))).sendKeys(password);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='confirmPassword']"))).sendKeys(confirmPassword);
        if (nickname != null) wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='nickname']"))).sendKeys(nickname);
        if (email != null) wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='email']"))).sendKeys(email);
        if (phone != null) wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='phone']"))).sendKeys(phone);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
    }

    @Test
    public void testRegisterSuccess() {
        String username = uniqueUsername();
        String email = username + "@test.com";
        String phone = "139" + (int)(Math.random() * 100000000);
        register(username, "123456", "123456", "昵称", email, phone);

        // 等待注册成功提示
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-message-success")));
        assertTrue(successMsg.getText().contains("注册成功"));
        // 跳转到登录页
        wait.until(ExpectedConditions.urlContains("/login"));
    }

    @Test
    public void testRegisterUsernameExists() {
        String username = "user1"; // 假设user1已存在
        register(username, "123456", "123456", "昵称", "unique" + UUID.randomUUID().toString().substring(0, 5) + "@test.com", "13800000001");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-alert-description")));
        assertTrue(alert.getText().contains("用户名已存在"));
    }

    @Test
    public void testRegisterEmailExists() {
        String username = uniqueUsername();
        String email = "user1@example.com"; // 假设该邮箱已存在
        register(username, "123456", "123456", "昵称", email, "13800000002");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-alert-description")));
        assertTrue(alert.getText().contains("邮箱已存在"));
    }

    @Test
    public void testRegisterPhoneExists() {
        String username = uniqueUsername();
        String phone = "13800138000"; // 假设该手机号已存在
        register(username, "123456", "123456", "昵称", "unique" + UUID.randomUUID().toString().substring(0, 5) + "@test.com", phone);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-alert-description")));
        assertTrue(alert.getText().contains("手机号已存在"));
    }

    @Test
    public void testRegisterPasswordMismatch() {
        String username = uniqueUsername();
        register(username, "123456", "654321", "昵称", "unique" + UUID.randomUUID().toString().substring(0, 5) + "@test.com", "13800000003");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement error = driver.findElement(By.cssSelector(".ant-form-item-explain-error"));
        assertTrue(error.getText().contains("两次输入的密码不一致"));
    }

    @Test
    public void testRegisterInvalidUsername() {
        driver.get("http://localhost:3000/register");
        driver.findElement(By.name("username")).sendKeys("a"); // 太短
        driver.findElement(By.name("password")).sendKeys("123456");
        driver.findElement(By.name("confirmPassword")).sendKeys("123456");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        WebElement error = driver.findElement(By.cssSelector(".ant-form-item-explain-error"));
        assertTrue(error.getText().contains("用户名至少4个字符"));
    }

    @Test
    public void testRegisterInvalidEmail() {
        driver.get("http://localhost:3000/register");
        driver.findElement(By.name("username")).sendKeys(uniqueUsername());
        driver.findElement(By.name("password")).sendKeys("123456");
        driver.findElement(By.name("confirmPassword")).sendKeys("123456");
        driver.findElement(By.name("email")).sendKeys("not-an-email");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        WebElement error = driver.findElement(By.cssSelector(".ant-form-item-explain-error"));
        assertTrue(error.getText().contains("请输入有效的邮箱地址"));
    }

    @Test
    public void testRegisterInvalidPhone() {
        driver.get("http://localhost:3000/register");
        driver.findElement(By.name("username")).sendKeys(uniqueUsername());
        driver.findElement(By.name("password")).sendKeys("123456");
        driver.findElement(By.name("confirmPassword")).sendKeys("123456");
        driver.findElement(By.name("phone")).sendKeys("12345");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        WebElement error = driver.findElement(By.cssSelector(".ant-form-item-explain-error"));
        assertTrue(error.getText().contains("请输入有效的手机号"));
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
