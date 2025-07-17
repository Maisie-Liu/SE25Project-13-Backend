import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;
import static org.junit.Assert.*;

public class ItemCommentSeleniumTest {
    private WebDriver driver;
    private String baseUrl = "http://localhost:3000";
    private String testItemTitle = "自动化测试物品" + System.currentTimeMillis();
    private String testComment = "这是自动化测试评论" + System.currentTimeMillis();
    private String testReply = "这是自动化测试回复" + System.currentTimeMillis();
    private String username = "user1";
    private String password = "123456";

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "F:/chromedriver/chromedriver-win64/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--headless=new");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        loginAsUser(username, password);
    }

    private void loginAsUser(String username, String password) {
        driver.get(baseUrl + "/login");
        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlToBe(baseUrl + "/"));
        assertEquals(baseUrl + "/", driver.getCurrentUrl());
    }

    @Test
    public void testCommentAndReplyOnItem() throws InterruptedException {
        // 直接使用已知物品ID
        String itemId = "22";
        assertNotNull(itemId);

        // 进入物品详情页
        driver.get(baseUrl + "/items/" + itemId);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("item-detail-comment-input")));

        // 发表评论
        WebElement commentInput = driver.findElement(By.name("item-detail-comment-input"));
        commentInput.clear();
        commentInput.sendKeys(testComment);
        WebElement submitBtn = driver.findElement(By.name("item-detail-comment-submit-btn"));
        submitBtn.click();
        Thread.sleep(1000);
        driver.navigate().refresh();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("item-detail-comment-input")));
        // 检查评论是否出现
        assertTrue(scrollToCommentByText(testComment));

        // 备选流：回复评论
        WebElement commentDiv = findCommentByText(testComment);
        assertNotNull(commentDiv);
        WebElement replyBtn = commentDiv.findElement(By.xpath(".//button[contains(.,'回复')]"));
        replyBtn.click();
        WebElement replyInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("item-detail-reply-input")));
        replyInput.clear();
        replyInput.sendKeys(testReply);
        WebElement replySubmitBtn = driver.findElement(By.name("item-detail-reply-submit-btn"));
        replySubmitBtn.click();
        Thread.sleep(1000);
        driver.navigate().refresh();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("item-detail-comment-input")));
        // 检查回复是否出现
        assertTrue(scrollToCommentByText(testReply));
    }

    // 滚动到包含指定文本的评论
    private boolean scrollToCommentByText(String text) {
        List<WebElement> comments = driver.findElements(By.cssSelector(".comment-item, .reply-item"));
        for (WebElement c : comments) {
            if (c.getText().contains(text)) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", c);
                return true;
            }
        }
        return false;
    }

    // 查找包含指定文本的评论节点
    private WebElement findCommentByText(String text) {
        List<WebElement> comments = driver.findElements(By.cssSelector(".comment-item, .reply-item"));
        for (WebElement c : comments) {
            if (c.getText().contains(text)) {
                return c;
            }
        }
        return null;
    }

    @After
    public void tearDown() {
        if (driver != null) driver.quit();
    }
} 