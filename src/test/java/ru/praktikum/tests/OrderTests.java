package ru.praktikum.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import ru.praktikum.pages.MainPage;
import ru.praktikum.pages.OrderPage;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderTests {

    private WebDriver driver;
    private MainPage mainPage;
    private OrderPage orderPage;

    @BeforeEach
    public void setUp() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        mainPage = new MainPage(driver);
        orderPage = new OrderPage(driver);
        mainPage.navigate();
    }

    @DisplayName("Заказ самоката через кнопку вверху")
    @ParameterizedTest(name = "Пользователь: {0} {1}")
    @CsvSource({
            "Иван,Иванов,Москва,+79991234567,16.12.2025,Сутки,Привезите быстрее",
            "Петр,Петров,Москва,+78125551234,17.12.2025,Сутки,Позвоните перед приездом"
    })
    public void testOrderFromTopButtonWithDifferentUsers(
            String name, String surname, String address,
            String phone, String date, String duration, String comment) {

        mainPage.clickOrderButtonTop();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        orderPage.fillFirstPart(name, surname, address, "Сокольники", phone);
        orderPage.clickNextButton();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        orderPage.selectDeliveryDate(date);
        orderPage.selectRentalDuration(duration);
        orderPage.selectBlackColor();
        orderPage.fillComment(comment);
        orderPage.clickOrderButton();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertTrue(orderPage.isSuccessMessageDisplayed(),
                "Должно появиться сообщение об успешном создании заказа");
    }

    @DisplayName("Заказ самоката через кнопку внизу")
    @ParameterizedTest(name = "Пользователь: {0} {1}")
    @CsvSource({
            "Анна,Смирнова,Москва,+79049876543,18.12.2025,Сутки,Спешу на встречу",
            "Сергей,Федоров,Москва,+79223334455,19.12.2025,Сутки,Без комментариев"
    })
    public void testOrderFromBottomButtonWithDifferentUsers(
            String name, String surname, String address,
            String phone, String date, String duration, String comment) {

        mainPage.clickOrderButtonBottom();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        orderPage.fillFirstPart(name, surname, address, "Домодедовская", phone);
        orderPage.clickNextButton();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        orderPage.selectDeliveryDate(date);
        orderPage.selectRentalDuration(duration);
        orderPage.selectGreyColor();
        orderPage.fillComment(comment);
        orderPage.clickOrderButton();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertTrue(orderPage.isSuccessMessageDisplayed(),
                "Должно появиться сообщение об успешном создании заказа");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}