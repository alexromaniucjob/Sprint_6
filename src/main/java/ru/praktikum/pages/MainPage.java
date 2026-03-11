package ru.praktikum.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.time.Duration;
import java.util.List;

import static ru.praktikum.Constants.BASE_URL;

public class MainPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Локаторы
    private final By allOrderButtons = By.xpath("//button[contains(text(), 'Заказать')]");
    private final By cookieButton = By.id("rcc-confirm-cookie"); // Более точный IDa
    private final By accordionQuestions = By.className("accordion__button");
    private final By accordionAnswers = By.className("accordion__panel");

    public MainPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void clickOrderButtonTop() {
        closeCookieConsent();
        List<WebElement> buttons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(allOrderButtons));
        if (!buttons.isEmpty()) {
            scrollToElement(buttons.get(0));
            buttons.get(0).click();
        }
    }

    public void clickOrderButtonBottom() {
        closeCookieConsent();
        List<WebElement> buttons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(allOrderButtons));
        if (buttons.size() > 1) {
            scrollToElement(buttons.get(1));
            buttons.get(1).click();
        }
    }

    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public void closeCookieConsent() {
        try {
            if (!driver.findElements(cookieButton).isEmpty()) {
                driver.findElement(cookieButton).click();
            }
        } catch (Exception e) {
            // Игнорируем, если кнопка исчезла
        }
    }

    public void clickQuestionButton(int index) {
        List<WebElement> questions = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(accordionQuestions));
        WebElement element = questions.get(index);

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", element);

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    public String getQuestionText(int index) {
        List<WebElement> questions = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(accordionQuestions));
        return questions.get(index).getText();
    }

    public String getAnswerText(int index) {
        List<WebElement> answers = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(accordionAnswers));
        WebElement answer = answers.get(index);

        wait.until(ExpectedConditions.visibilityOf(answer));
        return answer.getText();
    }

    public void navigate() {
        driver.get(BASE_URL);
        closeCookieConsent();
    }

    public boolean isAnswerVisible(int index) {
        try {
            List<WebElement> answers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(accordionAnswers));
            return wait.until(ExpectedConditions.visibilityOf(answers.get(index))).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}