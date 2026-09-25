package com.canteen.canteentokensystem.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium WebDriver tests covering the critical user journeys:
 * 1. Home page loads and navigation links work
 * 2. Menu page renders (items or empty state)
 * 3. Staff queue page renders (tokens or empty state)
 * 4. Dashboard page renders summary tiles
 * 5. Navigation between all pages via the nav bar
 *
 * On any assertion failure, a screenshot is saved to target/selenium-screenshots/.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserJourneySeleniumTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseUrl;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void teardown(TestInfo testInfo) {
        if (driver != null) {
            driver.quit();
        }
    }

    private void screenshotOnFailure(String testName) {
        try {
            Files.createDirectories(Paths.get("target/selenium-screenshots"));
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), Paths.get("target/selenium-screenshots/" + testName + ".png"));
        } catch (Exception e) {
            System.err.println("Could not save screenshot: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Journey 1: Home page loads with navigation links")
    void homePageLoadsWithNavigation() {
        try {
            driver.get(baseUrl + "/");
            assertTrue(driver.getTitle().contains("Canteen"), "Title should mention Canteen");

            List<WebElement> navLinks = driver.findElements(By.cssSelector("nav .links a"));
            assertEquals(3, navLinks.size(), "Expected 3 nav links (Menu, Queue, Dashboard)");
        } catch (AssertionError e) {
            screenshotOnFailure("homePageLoadsWithNavigation");
            throw e;
        }
    }

    @Test
    @DisplayName("Journey 2: Menu page renders items or empty state")
    void menuPageRenders() {
        try {
            driver.get(baseUrl + "/menu");
            assertTrue(driver.getTitle().contains("Menu"));

            boolean hasCards = !driver.findElements(By.cssSelector(".card-grid .card")).isEmpty();
            boolean hasEmptyState = !driver.findElements(By.cssSelector(".empty-state")).isEmpty();
            assertTrue(hasCards || hasEmptyState, "Menu should show items or an empty state");
        } catch (AssertionError e) {
            screenshotOnFailure("menuPageRenders");
            throw e;
        }
    }

    @Test
    @DisplayName("Journey 3: Staff queue page renders tokens or empty state")
    void queuePageRenders() {
        try {
            driver.get(baseUrl + "/queue");

            boolean hasTable = !driver.findElements(By.cssSelector("table")).isEmpty();
            boolean hasEmptyState = !driver.findElements(By.cssSelector(".empty-state")).isEmpty();
            assertTrue(hasTable || hasEmptyState, "Queue should show a table or an empty state");
        } catch (AssertionError e) {
            screenshotOnFailure("queuePageRenders");
            throw e;
        }
    }

    @Test
    @DisplayName("Journey 4: Dashboard shows summary tiles")
    void dashboardPageRenders() {
        try {
            driver.get(baseUrl + "/dashboard");

            List<WebElement> summaryCards = driver.findElements(By.cssSelector(".summary-card"));
            assertFalse(summaryCards.isEmpty(), "Dashboard should render at least one summary tile");
        } catch (AssertionError e) {
            screenshotOnFailure("dashboardPageRenders");
            throw e;
        }
    }

    @Test
    @DisplayName("Journey 5: Navigate across all pages via nav bar")
    void navigationBetweenPagesWorks() {
        try {
            driver.get(baseUrl + "/");

            driver.findElement(By.linkText("Menu")).click();
            assertTrue(driver.getCurrentUrl().endsWith("/menu"));

            driver.findElement(By.linkText("Staff Queue")).click();
            assertTrue(driver.getCurrentUrl().endsWith("/queue"));

            driver.findElement(By.linkText("Dashboard")).click();
            assertTrue(driver.getCurrentUrl().endsWith("/dashboard"));
        } catch (AssertionError e) {
            screenshotOnFailure("navigationBetweenPagesWorks");
            throw e;
        }
    }
}
