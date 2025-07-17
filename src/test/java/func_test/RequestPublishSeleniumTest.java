import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

import static org.junit.Assert.*;

public class RequestPublishSeleniumTest {
    private WebDriver driver;

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
        driver.get("http://localhost:3000/login");
        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlToBe("http://localhost:3000/"));
        assertEquals("http://localhost:3000/", driver.getCurrentUrl());
    }

    @Test
    public void testPublishRequestFromHome() throws InterruptedException {
        driver.get("http://localhost:3000/");
        // 点击悬浮按钮
        WebElement floatBtn = driver.findElement(By.className("floating-publish-btn"));
        floatBtn.click();
        // 选择“发布求购”
        WebElement requestBtn = driver.findElement(By.xpath("//span[text()='发布求购']"));
        requestBtn.click();

        fillAndSubmitRequestForm();
        // 跳转到求购论坛
        assertEquals("http://localhost:3000/requests", driver.getCurrentUrl());
        // 搜索刚刚发布的标题
        searchAndAssertRequestExists();
    }

    @Test
    public void testPublishRequestFromForum() throws InterruptedException {
        driver.get("http://localhost:3000/requests");
        // 点击“发布求购信息”按钮
        WebElement publishBtn = driver.findElement(By.xpath("//button[.//span[contains(text(),'发布求购信息')]]"));
        publishBtn.click();

        fillAndSubmitRequestForm();
        // 跳转到求购论坛
        assertEquals("http://localhost:3000/requests", driver.getCurrentUrl());
        // 搜索刚刚发布的标题
        searchAndAssertRequestExists();
    }

    @Test
    public void testRequestFormValidation() {
        driver.get("http://localhost:3000/publish-request");
        // 直接点击发布
        driver.findElement(By.name("request-submit")).click();
        // 检查表单校验提示
        List<WebElement> errors = driver.findElements(By.className("ant-form-item-explain-error"));
        assertFalse(errors.isEmpty());
    }

    private String requestTitle;

    private void fillAndSubmitRequestForm() throws InterruptedException {
        Thread.sleep(3000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        requestTitle = "自动化测试求购" + System.currentTimeMillis();

        // 标题
        WebElement titleInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("request-title")));
        titleInput.sendKeys(requestTitle);

        // 分类
        WebElement categorySelect = driver.findElement(By.name("request-category"));
        categorySelect.click();

        // 等待至少一个可选项内容出现（排除分组）
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".ant-select-item-option:not(.ant-select-item-group) .ant-select-item-option-content")
        ));

        // 获取所有可选项内容（排除分组）
        List<WebElement> optionContents = driver.findElements(By.cssSelector(
                ".ant-select-item-option:not(.ant-select-item-group) .ant-select-item-option-content"
        ));

        // 选择第一个可用分类
        if (!optionContents.isEmpty()) {
            // 这里用JS点击可以避免被遮挡问题
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", optionContents.get(0));
        } else {
            throw new RuntimeException("未加载到可用分类，请检查后端或网络！");
        }

        // 新旧程度
        WebElement conditionSelect = driver.findElement(By.name("request-condition"));
        conditionSelect.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".ant-select-item-option-content")
        ));
        List<WebElement> condOptions = driver.findElements(By.cssSelector(".ant-select-item-option-content"));
        if (!condOptions.isEmpty()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", condOptions.get(0));
        } else {
            throw new RuntimeException("未加载到新旧程度选项，请检查前端或网络！");
        }

        // 价格
        WebElement priceInput = driver.findElement(By.name("request-price"));
        priceInput.sendKeys("123");

        // 价格可议
        WebElement negoSelect = driver.findElement(By.name("request-negotiable"));
        negoSelect.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.cssSelector(".ant-select-item-option-content")
        ));
        List<WebElement> negoOptions = driver.findElements(By.cssSelector(".ant-select-item-option-content"));
        if (!negoOptions.isEmpty()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", negoOptions.get(0));
        } else {
            throw new RuntimeException("未加载到价格可议选项，请检查前端或网络！");
        }

        // 详情
        WebElement descInput = driver.findElement(By.name("request-description"));
        descInput.sendKeys("这是自动化测试发布的求购信息。");

        // 联系方式
        WebElement contactInput = driver.findElement(By.name("request-contact"));
        contactInput.sendKeys("test_contact");

        // 发布
        driver.findElement(By.name("request-submit")).click();
        Thread.sleep(500);
    }

    private void searchAndAssertRequestExists() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // 搜索框
        WebElement searchInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[placeholder='搜索求购信息']")));
        searchInput.clear();
        searchInput.sendKeys(requestTitle);
        // 点击搜索按钮
        WebElement searchBtn = driver.findElement(By.cssSelector(".ant-input-search-button"));
        searchBtn.click();
        // 检查列表中有该标题
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'post-card-title') and contains(.,'" + requestTitle + "')]")
        ));
        WebElement postTitle = driver.findElement(By.xpath("//div[contains(@class,'post-card-title') and contains(.,'" + requestTitle + "')]"));
        assertNotNull(postTitle);
    }

    @After
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}