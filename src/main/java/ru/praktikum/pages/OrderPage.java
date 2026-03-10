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
    private final By orderButton = By.xpath(".//div[contains(@class, 'Order_Buttons')]//button[text()='Заказать']");
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
    List<WebElement> inputs = driver.findElements(
            By.xpath("//input[@type='date' or contains(@placeholder,'Когда')]"));
    if (!inputs.isEmpty()) {
        WebElement dateInput = inputs.get(0);
        wait.until(ExpectedConditions.elementToBeClickable(dateInput));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = '" + date + "';", dateInput);
        dateInput.click();
        // Ждём, пока значение реально применится
        wait.until(d -> date.equals(dateInput.getAttribute("value")));
    }
}

    /**
     * Выбирает срок аренды
     */
    public void selectRentalDuration(String duration) {
        // Находим placeholder дропдауна "Срок аренды"
        WebElement dropdown = driver.findElement(
                By.xpath("//div[contains(@class,'Dropdown-placeholder') and contains(normalize-space(text()),'Срок аренды')]"));

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", dropdown);
        wait.until(ExpectedConditions.elementToBeClickable(dropdown)).click();

        // Берём первый доступный вариант срока аренды (позитивный сценарий)
        List<WebElement> options = wait.until(ExpectedConditions
                .presenceOfAllElementsLocatedBy(
                        By.xpath("//div[@class='Dropdown-menu']//div[contains(@class,'Dropdown-option')]")));
        if (!options.isEmpty()) {
            wait.until(ExpectedConditions.elementToBeClickable(options.get(0))).click();
        }
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
            String value = checkbox.getAttribute("value");
            String id = checkbox.getAttribute("id");

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
        WebElement element = driver.findElement(orderButton);

        // Прокручиваем страницу до кнопки (важно для Firefox)
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);

        // Кликаем по кнопке
        element.click();

        // В приложении после этого появляется модальное окно с подтверждением "Хотите оформить заказ?"
        // Нажимаем "Да", если такое окно появилось.
        try {
            WebElement yesButton = wait.until(ExpectedConditions.elementToBeClickable(confirmYesButton));
            yesButton.click();
        } catch (Exception e) {
            // Если модальное окно не появилось, просто продолжаем
        }
    }

    /**
     * Проверяет, появилось ли окно с сообщением об успешном заказе
     */
    public boolean isSuccessMessageDisplayed() {
        try {
            // Ищем текст "Заказ оформлен" внутри модального окна
            By successHeader = By.xpath("//div[contains(@class, 'Order_ModalHeader') and contains(text(), 'Заказ оформлен')]");
            return wait.until(ExpectedConditions.visibilityOfElementLocated(successHeader)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Получает текст сообщения об успешном заказе
     */
    public String getSuccessMessage() {
        try {
            List<WebElement> headings = driver.findElements(By.xpath("//h1 | //h2 | //h3"));
            for (WebElement heading : headings) {
                String text = heading.getText();
                if (text.contains("Заказ")) {
                    return text;
                }
            }
        } catch (Exception e) {
            // Сообщение не найдено
        }
        return "";
    }
}
