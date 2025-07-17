import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;


import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static org.junit.Assert.*;

public class ItemPublishSeleniumTest {
    private WebDriver driver;

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "F:/chromedriver/chromedriver-win64/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--headless=new");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        // 登录，假设已实现自动登录方法
        loginAsUser("user1", "123456");
    }

    private void loginAsUser(String username, String password) {
        driver.get("http://localhost:3000/login");
        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/"));
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.equals("http://localhost:3000/"));
    }

    @Test
    public void testPublishItemSuccess() {
        driver.get("http://localhost:3000/items/publish");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement nameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));

        nameInput.sendKeys("测试物品" + System.currentTimeMillis());
        // 选择分类
        driver.findElement(By.cssSelector(".ant-select-selector")).click();
        // 选择第一个可用分类
        List<WebElement> options = driver.findElements(By.cssSelector(".ant-select-item-option"));
        if (!options.isEmpty()) options.get(0).click();
        // 输入价格
        driver.findElement(By.name("price")).sendKeys("99.99");
        // 输入库存
        driver.findElement(By.name("stock")).clear();
        driver.findElement(By.name("stock")).sendKeys("2");
        // 选择新旧程度（假设有默认值可跳过）
        // 上传图片
        File img = new File("F:/data/images/headphone.jpg"); // 确保有一张测试图片
        // 直接查找 input[type='file'] 并上传
        WebElement uploadInput = driver.findElement(By.cssSelector("input[type='file']"));
        uploadInput.sendKeys(img.getAbsolutePath());
        // 等待图片上传完成
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-upload-list-item-done")));
        // 填写描述
        driver.findElement(By.id("description")).sendKeys("这是自动化测试发布的物品。");
        // 发布
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        // 检查成功提示
//        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/"));
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.equals("http://localhost:3000/"));
    }

    @Test
    public void testPublishItemMissingRequiredFields() {
        driver.get("http://localhost:3000/items/publish");
        // 直接点击发布
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        // 检查表单校验提示
        List<WebElement> errors = driver.findElements(By.className("ant-form-item-explain-error"));
        assertFalse(errors.isEmpty());
    }

    @Test
    public void testUploadInvalidImage() {
        driver.get("http://localhost:3000/items/publish");
        WebElement uploadBtn = driver.findElement(By.cssSelector("input[type='file']"));
        File txt = new File("F:/Desktop/123.txt"); // 非图片文件
        uploadBtn.sendKeys(txt.getAbsolutePath());
        // 检查错误提示
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.className("ant-message-error")));
    }

    @Test
    public void testGenerateDescriptionWithoutImage() {
        driver.get("http://localhost:3000/items/publish");
        // 点击AI自动生成描述
        WebElement aiBtn = driver.findElement(By.xpath("//button[.//span[contains(text(),'AI自动生成描述')]]"));
        assertTrue(aiBtn.getAttribute("disabled") != null);
    }

    @Test
    public void testDeleteImage() {
        driver.get("http://localhost:3000/items/publish");
        WebElement uploadBtn = driver.findElement(By.cssSelector("input[type='file']"));
        File img = new File("F:/data/images/headphone.jpg");
        uploadBtn.sendKeys(img.getAbsolutePath());
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-upload-list-item-done")));
        // 删除图片
        driver.findElement(By.cssSelector(".ant-upload-list-item .anticon-delete")).click();
        // 检查图片被移除
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".ant-upload-list-item")));
    }

    @After
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}