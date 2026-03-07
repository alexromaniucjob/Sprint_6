package ru.praktikum.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import ru.praktikum.pages.MainPage;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class FaqTests {

    private WebDriver driver;
    private MainPage mainPage;

    @BeforeEach
    public void setUp() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        driver.manage().window().maximize(); // Максимизация решает проблему перекрытия элементов
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        mainPage = new MainPage(driver);
        mainPage.navigate();
    }

    @ParameterizedTest(name = "Вопрос #{0}")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
    public void testFaqAccordionOpensCorrectAnswer(int questionIndex) {


        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Кликаем на вопрос
        mainPage.clickQuestionButton(questionIndex);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Проверяем что ответ видим
        assertTrue(mainPage.isAnswerVisible(questionIndex),
                "Ответ на вопрос номер " + (questionIndex + 1) + " должен быть виден");

        // Проверяем что ответ содержит текст
        String answerText = mainPage.getAnswerText(questionIndex);
        assertNotNull(answerText, "Текст ответа не должен быть null");
        assertFalse(answerText.trim().isEmpty(),
                "Текст ответа не должен быть пустой");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}