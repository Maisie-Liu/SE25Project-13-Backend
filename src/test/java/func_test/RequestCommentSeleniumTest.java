import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;
import java.util.List;
import static org.junit.Assert.*;

public class RequestCommentSeleniumTest {
    private WebDriver driver;
    private String baseUrl = "http://localhost:3000";
    private String testTitle = "自动化测试求购1752741297728";
    private String testComment = "这是自动化测试评论" + System.currentTimeMillis();
    private String testReply = "这是自动化测试回复" + System.currentTimeMillis();

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "F:/chromedriver/chromedriver-win64/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--headless=new");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        loginAsUser("user333", "123456");
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
    public void testCommentOnRequestFlow() throws InterruptedException {
        // 主页点击“求购论坛”
        driver.get(baseUrl + "/");
        WebElement forumBtn = driver.findElement(By.name("header-request-forum-btn"));
        forumBtn.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/requests"));
        assertTrue(driver.getCurrentUrl().contains("/requests"));

        // 滚动并找到指定求物帖卡片
        scrollToRequestCard(testTitle);
        WebElement card = findRequestCardByTitle(testTitle);
        assertNotNull(card);
        card.click();
        wait.until(ExpectedConditions.urlContains("/request/"));

        // 评论输入
        WebElement commentInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("request-detail-comment-input")));
        commentInput.clear();
        commentInput.sendKeys(testComment);
        WebElement submitBtn = driver.findElement(By.name("request-detail-comment-submit-btn"));
        submitBtn.click();
        Thread.sleep(1000);
        driver.navigate().refresh();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("request-detail-comment-input")));
        // 滚动到评论
        scrollToCommentByText(testComment);
        WebElement commentDiv = findCommentByText(testComment);
        assertNotNull(commentDiv);
        // 备选流：回复评论
        WebElement replyBtn = commentDiv.findElement(By.name("request-detail-comment-reply-btn"));
        replyBtn.click();
        WebElement replyInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("request-detail-reply-input")));
        replyInput.clear();
        replyInput.sendKeys(testReply);
        WebElement replySubmitBtn = driver.findElement(By.name("request-detail-reply-submit-btn"));
        replySubmitBtn.click();
        Thread.sleep(1000);
        driver.navigate().refresh();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("request-detail-comment-input")));
        scrollToCommentByText(testReply);
        WebElement replyDiv = findCommentByText(testReply);
        assertNotNull(replyDiv);
        // 备选流：删除评论（刷新后重新查找评论节点和删除按钮）
        driver.navigate().refresh();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.name("request-detail-comment-input")));
        scrollToCommentByText(testComment);
        WebElement commentDivForDelete = findCommentByText(testComment);
        assertNotNull(commentDivForDelete);
        WebElement delBtn = commentDivForDelete.findElement(By.name("request-detail-comment-delete-btn"));
        delBtn.click();
        Thread.sleep(1000);
        driver.navigate().refresh();
        Thread.sleep(1000);
        assertNull(findCommentByText(testComment));
    }

    private void scrollToRequestCard(String title) {
        List<WebElement> cards = driver.findElements(By.name("forum-request-card"));
        for (WebElement card : cards) {
            if (title.equals(card.getAttribute("data-title"))) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", card);
                return;
            }
        }
        // 如果没找到，尝试翻页或下拉加载（如有分页/加载更多）
        // 可根据实际情况补充
    }

    private WebElement findRequestCardByTitle(String title) {
        List<WebElement> cards = driver.findElements(By.name("forum-request-card"));
        for (WebElement card : cards) {
            if (title.equals(card.getAttribute("data-title"))) {
                return card;
            }
        }
        return null;
    }

    private void scrollToCommentByText(String text) {
        List<WebElement> comments = driver.findElements(By.cssSelector("[data-cid]"));
        for (WebElement c : comments) {
            if (c.getText().contains(text)) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", c);
                return;
            }
        }
    }

    private WebElement findCommentByText(String text) {
        List<WebElement> comments = driver.findElements(By.cssSelector("[data-cid]"));
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