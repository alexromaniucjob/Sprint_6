package ru.praktikum.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object для формы заказа
 */
public class OrderPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By firstNameInput = By.xpath("//input[@placeholder='* Имя']");
    private final By lastNameInput = By.xpath("//input[@placeholder='* Фамилия']");
    private final By addressInput = By.xpath("//input[@placeholder='* Адрес: куда привезти заказ']");
    private final By metroInput = By.xpath("//input[@placeholder='* Станция метро']");
    private final By phoneInput = By.xpath("//input[@placeholder='* Телефон: на него позвонит курьер']");
    private final By commentInput = By.xpath("//input[contains(@placeholder,'Комментарий') or contains(@placeholder,'комментарий')]");
    private final By allCheckboxes = By.xpath("//input[@type='checkbox']");
    private final By orderButton = By.xpath("//div[contains(@class, 'Order_Buttons')]//button[text()='Заказать']");
    private final By confirmYesButton = By.xpath("//button[text()='Да']");
    public OrderPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Заполняет первую часть формы заказа (личные данные)
     */
    public void fillFirstPart(String name, String surname, String address, String metroStation, String phone) {
        // Имя
        WebElement firstName = wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameInput));
        firstName.clear();
        firstName.sendKeys(name);

        // Фамилия
        WebElement lastName = driver.findElement(lastNameInput);
        lastName.clear();
        lastName.sendKeys(surname);

        // Адрес
        WebElement addressField = driver.findElement(addressInput);
        addressField.clear();
        addressField.sendKeys(address);

        // Станция метро: вводим текст и выбираем вариант из выпадающего списка
        WebElement metroField = driver.findElement(metroInput);
        metroField.click();
        metroField.clear();
        metroField.sendKeys(metroStation);
        // ждем появления вариантов в выпадающем списке
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                By.xpath("//div[contains(@class,'select-search__select')]//li")));
        metroField.sendKeys(Keys.ARROW_DOWN);
        metroField.sendKeys(Keys.ENTER);

        // Телефон
        WebElement phoneField = driver.findElement(phoneInput);
        phoneField.clear();
        phoneField.sendKeys(phone);
    }

    /**
     * Нажимает кнопку "Далее"
     */
    public void clickNextButton() {
        // Ищем кнопку с текстом "Далее"
        List<WebElement> buttons = driver.findElements(By.xpath("//button"));
        for (WebElement btn : buttons) {
            if (btn.getText().contains("Далее") || btn.getText().contains("далее")) {
                wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
                // Ждём появления заголовка второго шага
                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(text(),'Про аренду')]")));
                return;
            }
        }

        // Если не нашли по тексту, берём вторую кнопку на форме
        if (buttons.size() > 1) {
            WebElement fallback = buttons.get(1);
            wait.until(ExpectedConditions.elementToBeClickable(fallback)).click();
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(text(),'Про аренду')]")));
        }
    }

    /**
     * Выбирает дату доставки
     */
    public void selectDeliveryDate(String date) {
        // На стенде поле даты — текстовый инпут с календарём, надежнее вводить дату как текст
        WebElement dateInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[contains(@placeholder,'Когда')]")));
        dateInput.click();
        dateInput.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        dateInput.sendKeys(date);
        dateInput.sendKeys(Keys.ENTER);

        wait.until(d -> {
            String value = dateInput.getDomProperty("value");
            return value != null && !value.trim().isEmpty();
        });
    }

    /**
     * Выбирает срок аренды
     */
    public void selectRentalDuration(String duration) {
        // Открываем дропдаун
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[contains(@class,'Dropdown-placeholder')]")));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", dropdown);
        dropdown.click();

        // Ждём появления меню и ищем вариант по тексту (регистронезависимо)
        List<WebElement> options = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.xpath("//div[contains(@class,'Dropdown-option')]")));

        for (WebElement option : options) {
            if (option.getText().trim().equalsIgnoreCase(duration)) {
                option.click();
                return;
            }
        }

        // Если не нашли — берём первый вариант
        options.get(0).click();
    }

    /**
     * Выбирает цвет самоката (чёрный)
     */
    public void selectBlackColor() {
        selectColorByValue("black");
    }

    /**
     * Выбирает цвет самоката (серый)
     */
    public void selectGreyColor() {
        selectColorByValue("grey");
    }

    /**
     * Выбирает цвет по значению
     */
    private void selectColorByValue(String colorValue) {
        List<WebElement> checkboxes = driver.findElements(allCheckboxes);
        for (WebElement checkbox : checkboxes) {
            String value = checkbox.getDomAttribute("value");
            String id = checkbox.getDomAttribute("id");

            if ((value != null && value.contains(colorValue)) ||
                    (id != null && id.contains(colorValue))) {
                if (!checkbox.isSelected()) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
                    wait.until(d -> checkbox.isSelected());
                }
                return;
            }
        }
    }

    /**
     * Заполняет комментарий для курьера
     */
    public void fillComment(String comment) {
        WebElement commentField = driver.findElement(commentInput);
        commentField.clear();
        commentField.sendKeys(comment);
    }

    /**
     * Нажимает кнопку "Заказать"
     */
    public void clickOrderButton() {
        // Находим основную кнопку оформления заказа
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(orderButton));

        // Прокручиваем страницу до кнопки (важно для Firefox)
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);

        // Кликаем по кнопке
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);

        // В приложении после этого появляется модальное окно с подтверждением "Хотите оформить заказ?"
        // Нажимаем "Да", если такое окно появилось.
        try {
            WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(confirmYesButton));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", yesButton);
        } catch (Exception e) {
            // Если модальное окно не появилось, просто продолжаем
        }
    }

    /**
     * Текст видимого заголовка модального окна заказа (если есть).
     */
    public String getModalHeaderTextIfPresent() {
        try {
            List<WebElement> headers = driver.findElements(By.xpath("//div[contains(@class,'Order_ModalHeader')]"));
            for (WebElement header : headers) {
                if (header.isDisplayed()) {
                    return header.getText();
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Текст видимого тела модального окна (если есть) — для диагностики при падении теста.
     */
    public String getModalBodyTextIfPresent() {
        try {
            List<WebElement> bodies = driver.findElements(By.xpath("//div[contains(@class,'Order_Modal') or contains(@class,'Modal') or contains(@class,'modal')]"));
            for (WebElement body : bodies) {
                if (body.isDisplayed()) {
                    return body.getText();
                }
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Сводка по видимым модалкам для сообщения assert (использует {@link #getModalHeaderTextIfPresent()} и {@link #getModalBodyTextIfPresent()}).
     */
    public String getOrderSuccessDiagnostics() {
        return "modalHeader='" + getModalHeaderTextIfPresent() + "', modalBody='" + getModalBodyTextIfPresent() + "'";
    }

    /**
     * Ждёт появления подтверждения успешного заказа: сначала проверяет заголовок модалки
     * ({@link #getModalHeaderTextIfPresent()}), затем — любой видимый элемент с текстом «Заказ оформлен».
     */
    public boolean isSuccessMessageDisplayed() {
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        try {
            return longWait.until(d -> {
                String header = getModalHeaderTextIfPresent();
                if (header != null && header.contains("Заказ оформлен")) {
                    return true;
                }
                List<WebElement> candidates = driver.findElements(
                        By.xpath("//*[contains(normalize-space(.),'Заказ оформлен')]"));
                for (WebElement el : candidates) {
                    try {
                        if (el.isDisplayed()) {
                            return true;
                        }
                    } catch (Exception ignored) {
                        // stale / not interactable
                    }
                }
                return false;
            });
        } catch (Exception e) {
            return false;
        }
    }
}
