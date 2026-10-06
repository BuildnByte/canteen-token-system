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
import org.springframework.beans.factory.annotation.Autowired;

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
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
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

    @Autowired
    private com.canteen.canteentokensystem.service.UserService userService;

    private String createAdminAndLogin() {
        String email = "admin" + System.currentTimeMillis() + "@test.com";
        com.canteen.canteentokensystem.dto.AuthDtos.RegisterRequest req = 
            new com.canteen.canteentokensystem.dto.AuthDtos.RegisterRequest("Admin Test", email, "password123", com.canteen.canteentokensystem.model.Role.ADMIN);
        com.canteen.canteentokensystem.model.User user = userService.register(req);
        userService.updateUserRole(user.getId(), com.canteen.canteentokensystem.model.Role.ADMIN);

        driver.get(baseUrl + "/login");
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys("password123");
        WebElement loginBtn = driver.findElement(By.id("submit-btn"));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", loginBtn);
        
        // Wait for redirect to dashboard
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> d.getCurrentUrl().endsWith("/dashboard"));
        return email;
    }

    @Test
    @DisplayName("Journey 1: Home page loads with navigation links for guest")
    void homePageLoadsWithNavigation() {
        try {
            driver.get(baseUrl + "/");
            assertTrue(driver.getTitle().contains("Canteen"), "Title should mention Canteen");

            List<WebElement> navLinks = driver.findElements(By.cssSelector(".nav-links a"));
            assertFalse(navLinks.isEmpty(), "Guest should see nav links (e.g. Menu)");
            
            WebElement signIn = driver.findElement(By.linkText("Sign in"));
            assertNotNull(signIn, "Guest should see Sign in button");
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

            boolean hasCards = !driver.findElements(By.cssSelector(".card-grid .menu-card")).isEmpty();
            boolean hasEmptyState = !driver.findElements(By.cssSelector(".empty-state")).isEmpty();
            assertTrue(hasCards || hasEmptyState, "Menu should show items or an empty state");
        } catch (AssertionError e) {
            screenshotOnFailure("menuPageRenders");
            throw e;
        }
    }

    @Test
    @DisplayName("Journey 3: Student auth journey and role enforcement")
    void authJourneyAndRoleEnforcement() {
        try {
            String email = "student" + System.currentTimeMillis() + "@test.com";
            driver.get(baseUrl + "/register");
            driver.findElement(By.id("name")).sendKeys("Test Student");
            driver.findElement(By.id("email")).sendKeys(email);
            driver.findElement(By.id("password")).sendKeys("password123");
            WebElement submitBtn = driver.findElement(By.id("submit-btn"));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);

            // Wait for redirect to /menu (since students go to menu)
            new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(d -> d.getCurrentUrl().endsWith("/menu"));
            
            // Attempt to go to dashboard (admin only)
            driver.get(baseUrl + "/dashboard");
            
            // Should be redirected back to /menu by JS role enforcement
            new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(d -> d.getCurrentUrl().endsWith("/menu"));
                    
        } catch (AssertionError e) {
            screenshotOnFailure("authJourneyAndRoleEnforcement");
            throw e;
        }
    }

    @Test
    @DisplayName("Journey 4: Admin Dashboard renders summary tiles")
    void dashboardPageRenders() {
        try {
            createAdminAndLogin();

            List<WebElement> summaryCards = driver.findElements(By.cssSelector(".stat-card"));
            assertFalse(summaryCards.isEmpty(), "Dashboard should render at least one stat card");
        } catch (AssertionError e) {
            screenshotOnFailure("dashboardPageRenders");
            throw e;
        }
    }

    @Test
    @DisplayName("Journey 5: Admin navigation between pages works")
    void navigationBetweenPagesWorks() {
        try {
            createAdminAndLogin();

            WebElement menuLink = driver.findElement(By.cssSelector("a[href='/admin/menu']"));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", menuLink);
            new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(d -> d.getCurrentUrl().endsWith("/admin/menu"));

            WebElement queueLink = driver.findElement(By.cssSelector("a[href='/staff/queue']"));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", queueLink);
            new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(d -> d.getCurrentUrl().endsWith("/staff/queue"));

            WebElement dashLink = driver.findElement(By.cssSelector("a[href='/dashboard']"));
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", dashLink);
            new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(d -> d.getCurrentUrl().endsWith("/dashboard"));
        } catch (AssertionError e) {
            screenshotOnFailure("navigationBetweenPagesWorks");
            throw e;
        }
    }
}
