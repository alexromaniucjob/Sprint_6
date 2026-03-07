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

    private WebDriver driver;
    private WebDriverWait wait;

    // Находим все инпуты и работаем с ними по индексам
    private By allInputs = By.xpath("//input");
    private By allCheckboxes = By.xpath("//input[@type='checkbox']");
    private By orderButton = By.xpath(".//div[contains(@class, 'Order_Buttons')]//button[text()='Заказать']");
    private By confirmYesButton = By.xpath("//button[text()='Да']");

    public OrderPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Заполняет первую часть формы заказа (личные данные)
     */
    public void fillFirstPart(String name, String surname, String address, String metroStation, String phone) {
        try {
            Thread.sleep(500); // Ждём загрузки формы
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Имя
        WebElement firstNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[contains(@placeholder,'Имя')]")));
        firstNameInput.clear();
        firstNameInput.sendKeys(name);

        // Фамилия
        WebElement lastNameInput = driver.findElement(
                By.xpath("//input[contains(@placeholder,'Фамилия')]"));
        lastNameInput.clear();
        lastNameInput.sendKeys(surname);

        // Адрес
        WebElement addressInput = driver.findElement(
                By.xpath("//input[contains(@placeholder,'Адрес')]"));
        addressInput.clear();
        addressInput.sendKeys(address);

        // Станция метро: вводим текст и выбираем вариант из выпадающего списка
        WebElement metroInput = driver.findElement(
                By.xpath("//input[contains(@placeholder,'станция метро') or contains(@placeholder,'Станция метро') or contains(@placeholder,'метро')]"));
        metroInput.click();
        metroInput.clear();
        metroInput.sendKeys(metroStation);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        metroInput.sendKeys(Keys.ARROW_DOWN);
        metroInput.sendKeys(Keys.ENTER);

        // Телефон
        WebElement phoneInput;
        try {
            phoneInput = driver.findElement(
                    By.xpath("//input[@type='tel' or contains(@placeholder,'Телефон') or contains(@placeholder,'телефон')]"));
        } catch (Exception e) {
            // На некоторых версиях формы тип может быть text, поэтому берём последний видимый инпут как fallback
            List<WebElement> allInputs = driver.findElements(By.xpath("//input"));
            phoneInput = allInputs.get(allInputs.size() - 1);
        }
        phoneInput.clear();
        phoneInput.sendKeys(phone);
    }

    /**
     * Нажимает кнопку "Далее"
     */
    public void clickNextButton() {
        try {
            // Ищем кнопку с текстом "Далее"
            List<WebElement> buttons = driver.findElements(By.xpath("//button"));
            for (WebElement btn : buttons) {
                if (btn.getText().contains("Далее") || btn.getText().contains("далее")) {
                    wait.until(ExpectedConditions.elementToBeClickable(btn)).click();
                    Thread.sleep(500);
                    return;
                }
            }

            // Если не нашли по тексту, берём вторую кнопку на форме
            if (buttons.size() > 1) {
                wait.until(ExpectedConditions.elementToBeClickable(buttons.get(1))).click();
                Thread.sleep(500);
            }
        } catch (Exception e) {
            // Ошибка при нажатии
        }
    }

    /**
     * Выбирает дату доставки
     */
    public void selectDeliveryDate(String date) {
        try {
            List<WebElement> inputs = driver.findElements(By.xpath("//input[@type='date'] | //input[@placeholder*='Когда']"));
            if (!inputs.isEmpty()) {
                WebElement dateInput = inputs.get(0);
                ((JavascriptExecutor) driver).executeScript("arguments[0].value = '" + date + "';", dateInput);
                dateInput.click();
                Thread.sleep(300);
            }
        } catch (Exception e) {
            // Дата может быть выбрана по-другому
        }
    }

    /**
     * Выбирает срок аренды
     */
    public void selectRentalDuration(String duration) {
        try {
            // Находим placeholder дропдауна "Срок аренды"
            WebElement dropdown = driver.findElement(
                    By.xpath("//div[contains(@class,'Dropdown-placeholder') and contains(normalize-space(text()),'Срок аренды')]"));

            // Прокручиваем к элементу и кликаем по нему
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", dropdown);
            dropdown.click();

            Thread.sleep(500); // ждём открытия выпадающего списка

            // Берём первый доступный вариант срока аренды
            List<WebElement> options = driver.findElements(
                    By.xpath("//div[@class='Dropdown-menu']//div[contains(@class,'Dropdown-option')]"));
            if (!options.isEmpty()) {
                options.get(0).click();
            }

            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
        try {
            List<WebElement> checkboxes = driver.findElements(allCheckboxes);
            for (WebElement checkbox : checkboxes) {
                String value = checkbox.getAttribute("value");
                String id = checkbox.getAttribute("id");

                if ((value != null && value.contains(colorValue)) ||
                        (id != null && id.contains(colorValue))) {
                    if (!checkbox.isSelected()) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", checkbox);
                        Thread.sleep(300);
                    }
                    return;
                }
            }
        } catch (Exception e) {
            // Цвет может быть выбран по-другому
        }
    }

    /**
     * Заполняет комментарий для курьера
     */
    public void fillComment(String comment) {
        try {
            List<WebElement> inputs = driver.findElements(allInputs);

            // Ищем инпут с placeholder содержащим "комментарий"
            for (WebElement input : inputs) {
                String placeholder = input.getAttribute("placeholder");
                if (placeholder != null && placeholder.toLowerCase().contains("комментарий")) {
                    input.sendKeys(comment);
                    return;
                }
            }

            // Если не нашли, берём последний text инпут перед кнопкой
            List<WebElement> textInputs = new java.util.ArrayList<>();
            for (WebElement input : inputs) {
                if (input.isDisplayed() && "text".equals(input.getAttribute("type"))) {
                    textInputs.add(input);
                }
            }

            if (!textInputs.isEmpty()) {
                textInputs.get(textInputs.size() - 1).sendKeys(comment);
            }
        } catch (Exception e) {
            // Комментарий не заполнен
        }
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
