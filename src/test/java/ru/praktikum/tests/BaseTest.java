package ru.praktikum.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;

/**
 * Базовый класс тестов: создание WebDriver по системному свойству {@code browser}.
 * <ul>
 *     <li>{@code firefox} — по умолчанию (в т.ч. через {@code pom.xml})</li>
 *     <li>{@code chrome} — {@code mvn test -Dbrowser=chrome}</li>
 * </ul>
 * На стенде «Самокат» известен баг оформления заказа только в Chrome — тест может упасть; это ожидаемо.
 */
public class BaseTest {

    private static final String BROWSER_PROPERTY = "browser";

    protected WebDriver driver;

    protected void setUpDriver() {
        String browser = System.getProperty(BROWSER_PROPERTY, "firefox").trim().toLowerCase();

        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "firefox":
            default:
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
        }

        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
