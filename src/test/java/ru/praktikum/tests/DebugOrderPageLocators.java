package ru.praktikum.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import ru.praktikum.pages.MainPage;
import ru.praktikum.pages.OrderPage;

import java.time.Duration;
import java.util.List;

/**
 * Вспомогательный тест для отладки локаторов второй части формы заказа.
 * Запускается отдельно: mvn -q -Dtest=DebugOrderPageLocators test
 */
public class DebugOrderPageLocators {

    private WebDriver driver;
    private MainPage mainPage;

    @BeforeEach
    public void setUp() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        mainPage = new MainPage(driver);
        mainPage.navigate();
    }

    @Test
    public void printSecondStepStructure() throws Exception {
        System.out.println("=== DEBUG: start order flow to second step ===");

        mainPage.clickOrderButtonTop();
        Thread.sleep(1000);

        // Инпуты на первой странице формы
        System.out.println("=== DEBUG: first step inputs ===");
        List<WebElement> firstStepInputs = driver.findElements(By.xpath("//input"));
        System.out.println("Inputs count: " + firstStepInputs.size());
        for (int i = 0; i < firstStepInputs.size(); i++) {
            WebElement inp = firstStepInputs.get(i);
            if (i >= 10) {
                System.out.println("  ...");
                break;
            }
            System.out.println("  [" + i + "] type=" + inp.getAttribute("type")
                    + ", placeholder='" + inp.getAttribute("placeholder")
                    + "', name=" + inp.getAttribute("name")
                    + ", id=" + inp.getAttribute("id") + "'");
        }

        OrderPage orderPage = new OrderPage(driver);
        orderPage.fillFirstPart("Тест", "Пользователь", "Москва, тестовая улица, 1", "Сокольники", "+79991234567");
        orderPage.clickNextButton();
        Thread.sleep(1500);

        System.out.println("=== DEBUG: looking for rental controls ===");

        // Выводим все элементы, в тексте которых встречается "Срок аренды"
        List<WebElement> labels = driver.findElements(By.xpath("//*[contains(text(),'Срок аренды')]"));
        System.out.println("Labels with 'Срок аренды': " + labels.size());
        for (int i = 0; i < labels.size(); i++) {
            WebElement el = labels.get(i);
            System.out.println("  [" + i + "] tag=" + el.getTagName() + ", class=" + el.getAttribute("class"));
        }

        // Все select-элементы и их опции
        List<WebElement> selects = driver.findElements(By.tagName("select"));
        System.out.println("Select elements count: " + selects.size());
        for (int i = 0; i < selects.size(); i++) {
            WebElement select = selects.get(i);
            System.out.println("  SELECT[" + i + "]: id=" + select.getAttribute("id")
                    + ", name=" + select.getAttribute("name")
                    + ", class=" + select.getAttribute("class"));
            List<WebElement> options = select.findElements(By.tagName("option"));
            for (WebElement option : options) {
                System.out.println("    option value=" + option.getAttribute("value")
                        + ", text=" + option.getText());
            }
        }

        // Любые div-элементы, где в классе фигурирует "Dropdown"
        List<WebElement> dropdowns = driver.findElements(By.xpath("//div[contains(@class,'Dropdown')]"));
        System.out.println("Divs with class containing 'Dropdown': " + dropdowns.size());
        for (int i = 0; i < dropdowns.size(); i++) {
            WebElement el = dropdowns.get(i);
            if (i >= 10) {
                System.out.println("  ...");
                break;
            }
            System.out.println("  [" + i + "] class=" + el.getAttribute("class")
                    + ", text='" + el.getText() + "'");
        }

        // Кликаем по placeholder срока аренды и смотрим, что появилось
        System.out.println("=== DEBUG: click dropdown placeholder and inspect menu ===");
        WebElement placeholder = driver.findElement(By.xpath("//div[contains(@class,'Dropdown-placeholder')]"));
        placeholder.click();
        Thread.sleep(1000);

        List<WebElement> menus = driver.findElements(By.xpath("//div[contains(@class,'Dropdown-menu')]"));
        System.out.println("Dropdown-menu count: " + menus.size());
        for (int i = 0; i < menus.size(); i++) {
            WebElement menu = menus.get(i);
            System.out.println("  MENU[" + i + "] class=" + menu.getAttribute("class")
                    + ", text='" + menu.getText() + "'");
        }

        List<WebElement> options = driver.findElements(By.xpath("//div[contains(@class,'Dropdown-option')]"));
        System.out.println("Dropdown-option count: " + options.size());
        for (int i = 0; i < options.size(); i++) {
            WebElement opt = options.get(i);
            System.out.println("  OPTION[" + i + "] text='" + opt.getText() + "', class=" + opt.getAttribute("class") + "'");
        }

        // Пробуем полностью оформить заказ и выводим результат
        System.out.println("=== DEBUG: try to finish order and print success modal ===");
        orderPage.selectDeliveryDate("16.12.2025");
        orderPage.selectRentalDuration("Сутки");
        orderPage.selectBlackColor();
        orderPage.fillComment("debug comment");
        orderPage.clickOrderButton();

        Thread.sleep(3000);
        System.out.println("Success visible: " + orderPage.isSuccessMessageDisplayed());
        System.out.println("Success text: '" + orderPage.getSuccessMessage() + "'");

        System.out.println("=== DEBUG: end ===");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}

