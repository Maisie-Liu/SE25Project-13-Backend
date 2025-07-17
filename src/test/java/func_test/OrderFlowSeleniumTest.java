import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import static org.junit.Assert.*;

public class OrderFlowSeleniumTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "F:/chromedriver/chromedriver-win64/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--headless=new");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().setSize(new Dimension(1400, 900)); // 新增
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private void loginAs(String username, String password) {
        driver.get("http://localhost:3000/login");
        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/"));
    }

    private void logoutIfNeeded() {
        // 先尝试点击右上角用户菜单退出
        try {
            // 展开用户菜单
            WebElement avatar = driver.findElement(By.cssSelector(".user-avatar-container, .ant-avatar, .username"));
            avatar.click();
            // 找到“退出登录”按钮并点击
            WebElement logoutBtn = driver.findElement(By.xpath("//*[contains(text(),'退出登录')]"));
            logoutBtn.click();
            // 等待跳转到登录页
            wait.until(ExpectedConditions.urlContains("/login"));
        } catch (Exception e) {
            // 如果没找到，说明本来就未登录，可以忽略
        }
        // 兜底：清除localStorage
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        ((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");
        driver.get("http://localhost:3000/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
    }

    private void getOrderDetailFromHomePage(String itemName) throws InterruptedException {
        // 消息提示有未读
        WebElement notifyBtn = wait.until(ExpectedConditions.elementToBeClickable(By.name("header-notification-btn")));
        WebElement badge = driver.findElement(By.cssSelector(".ant-badge .ant-scroll-number-only-unit.current"));
        assertTrue(badge.isDisplayed());
        notifyBtn.click();
        // 进入订单消息
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("order-message-card")));
        driver.findElement(By.name("order-message-card")).click();
        // 找到刚刚下单的订单卡片（通过物品名）
        // 滚动到底部，确保所有订单卡片加载出来
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        Thread.sleep(500);
        List<WebElement> orderCards = driver.findElements(By.cssSelector("div[name^='order-message-card-']"));
        boolean found = false;
        for (WebElement card : orderCards) {
            try {
                WebElement strong = card.findElement(By.cssSelector(".order-message-item-details strong"));
                if (strong.getText().contains(itemName)) {
                    WebElement viewBtn = card.findElement(By.name("order-view-btn"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", viewBtn);
                    Thread.sleep(200);
                    viewBtn.click();
                    found = true;
                    break;
                }
            } catch (NoSuchElementException ignore) {}
        }
        assertTrue("未找到刚刚下单的订单卡片", found);
        Thread.sleep(500);
    }


    @Test
    public void testMessage() throws InterruptedException {
        loginAs("admin", "123456");
        getOrderDetailFromHomePage("高等数学教材，配习题集");
        WebElement confirmOrderBtn = wait.until(ExpectedConditions.elementToBeClickable(By.name("order-comment-buyer-btn")));
        confirmOrderBtn.click();
        File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            Files.copy(screenshot.toPath(), new File("debug.png").toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException io_e) {
            throw new RuntimeException(io_e);
        }
    }

    @Test
    public void testOrderMainFlow() throws InterruptedException {
        // 1. 买家登录并下单
        loginAs("admin", "123456");
        driver.get("http://localhost:3000/items/20");
        // 点击立即预订
        WebElement orderBtn = wait.until(ExpectedConditions.elementToBeClickable(By.name("order-btn")));
        orderBtn.click();
        // 填写交易地点和留言
        WebElement locationInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("order-trade-location")));
        locationInput.sendKeys("图书馆门口");
        WebElement messageInput = driver.findElement(By.name("order-buyer-message"));
        messageInput.sendKeys("请明天下午交易");
        // 提交
        WebElement confirmBtn = driver.findElement(By.name("order-confirm-btn"));
        confirmBtn.click();
        // 等待下单成功提示
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.name("order-trade-location")));

        // 2. 卖家登录，确认订单
        logoutIfNeeded();
        loginAs("user1", "123456");
        getOrderDetailFromHomePage("测试物品1752672233756");
        // 确认订单
        WebElement confirmOrderBtn = wait.until(ExpectedConditions.elementToBeClickable(By.name("order-confirm-btn")));
        confirmOrderBtn.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'订单已确认')] | //span[contains(text(),'待收货')]")));

        // 3. 买家再次登录，确认收货
        logoutIfNeeded();
        loginAs("admin", "123456");
        getOrderDetailFromHomePage("测试物品1752672233756");
        WebElement receiveBtn = wait.until(ExpectedConditions.elementToBeClickable(By.name("order-receive-btn")));
        receiveBtn.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'确认收货成功')] | //span[contains(text(),'待评价')]")));
        // 新增：等待并点击“我知道了”按钮
        WebElement knownBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[span[normalize-space(text())='我知道了']]")
        ));
        knownBtn.click();

        // 4. 买家评价卖家
        // 等待页面刷新后“评价卖家”按钮可点击
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".ant-modal-root, .ant-modal-wrap"))); // 等待弹窗消失
        wait.until(ExpectedConditions.elementToBeClickable(By.name("order-comment-seller-btn")));
        Thread.sleep(3000); // 可选，确保动画完成
        WebElement commentSellerBtn = driver.findElement(By.name("order-comment-seller-btn"));
        try {
            commentSellerBtn.click();
        } catch (ElementClickInterceptedException e) {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            try {
                Files.copy(screenshot.toPath(), new File("debug.png").toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException io_e) {
                throw new RuntimeException(io_e);
            }
            throw e;
        }
        // 自动点击评分星星（如5星）
        WebElement starsUl = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-rate.rating-stars")));
        List<WebElement> starRadios = starsUl.findElements(By.cssSelector("li.ant-rate-star > div[role='radio']"));
        if (starRadios.size() >= 5) {
            starRadios.get(4).click(); // 5星
        }
        WebElement commentInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("order-comment-input")));
        commentInput.sendKeys("卖家服务很好");
        WebElement commentSubmitBtn = driver.findElement(By.name("order-comment-submit-btn"));
        commentSubmitBtn.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.name("order-comment-input")));

        // 5. 卖家评价买家
        logoutIfNeeded();
        loginAs("user1", "123456");
        getOrderDetailFromHomePage("测试物品1752672233756");
        WebElement commentBuyerBtn = wait.until(ExpectedConditions.elementToBeClickable(By.name("order-comment-buyer-btn")));
        commentBuyerBtn.click();
        // 自动点击评分星星（如5星）
        WebElement starsUl2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".ant-rate.rating-stars")));
        List<WebElement> starRadios2 = starsUl2.findElements(By.cssSelector("li.ant-rate-star > div[role='radio']"));
        if (starRadios2.size() >= 5) {
            starRadios2.get(4).click(); // 5星
        }
        WebElement commentInput2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("order-comment-input")));
        commentInput2.sendKeys("买家很守时");
        WebElement commentSubmitBtn2 = driver.findElement(By.name("order-comment-submit-btn"));
        commentSubmitBtn2.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.name("order-comment-input")));
    }

    @Test
    public void testBuyerCannotOrderOwnItem() {
        loginAs("admin", "123456");
        driver.get("http://localhost:3000/items/8");
        WebElement orderBtn = wait.until(ExpectedConditions.elementToBeClickable(By.name("order-btn")));
        orderBtn.click();
        // 检查弹窗提示不能购买自己物品
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'不能购买自己的商品')]")));
        assertNotNull(errorMsg);
    }

    @Test
    public void testCancelOrderFlow() throws InterruptedException {
        // 买家下单
        loginAs("admin", "123456");
        driver.get("http://localhost:3000/items/19");
        WebElement orderBtn = wait.until(ExpectedConditions.elementToBeClickable(By.name("order-btn")));
        orderBtn.click();
        WebElement locationInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("order-trade-location")));
        locationInput.sendKeys("图书馆门口");
        WebElement messageInput = driver.findElement(By.name("order-buyer-message"));
        messageInput.sendKeys("请明天下午交易");
        WebElement confirmBtn = driver.findElement(By.name("order-confirm-btn"));
        confirmBtn.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.name("order-trade-location")));
        // 卖家登录，终止订单
        logoutIfNeeded();
        loginAs("user1", "123456");
        getOrderDetailFromHomePage("测试物品1752672204668");
        WebElement cancelBtn = wait.until(ExpectedConditions.elementToBeClickable(By.name("order-cancel-btn")));
        cancelBtn.click();
        WebElement reasonSelect = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("order-cancel-reason")));
        reasonSelect.click();
        Thread.sleep(300);
        // 选择第一个原因
        List<WebElement> options = driver.findElements(By.cssSelector(".ant-select-item-option-content"));
        if (!options.isEmpty()) options.get(0).click();
        WebElement submitBtn = driver.findElement(By.xpath("//button[@name='order-cancel-submit-btn' or @type='button' and contains(.,'提 交')]"));
        submitBtn.click();
        // 检查订单状态变为已取消/已拒绝
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'订单已被取消')]")));
    }

    @After
    public void tearDown() {
        if (driver != null) driver.quit();
    }
} 