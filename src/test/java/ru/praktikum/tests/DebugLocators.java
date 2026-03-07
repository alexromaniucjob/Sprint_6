package ru.praktikum.tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;
import java.util.List;

/**
 * Отладочный скрипт для поиска реальных локаторов
 * Запусти этот класс как обычный Java main метод
 */
public class DebugLocators {

    public static void main(String[] args) {
        WebDriverManager.firefoxdriver().setup();
        WebDriver driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            driver.get("https://qa-scooter.praktikum-services.ru/");
            Thread.sleep(3000); // Ждём загрузки

            System.out.println("\n" + "=".repeat(100));
            System.out.println("ОТЛАДКА СТРУКТУРЫ СТРАНИЦЫ");
            System.out.println("=".repeat(100));

            // 1. Проверяем все div элементы
            System.out.println("\n1. ВСЕ DIV ЭЛЕМЕНТЫ СО СПЕЦИАЛЬНЫМИ КЛАССАМИ:");
            System.out.println("-".repeat(100));
            List<WebElement> allDivs = driver.findElements(By.xpath("//div[contains(@class, 'ccordion') or contains(@class, 'ccord') or contains(@class, 'panel')]"));
            System.out.println("Найдено div-ов с 'ccordion'/'ccord'/'panel': " + allDivs.size());
            for (int i = 0; i < Math.min(3, allDivs.size()); i++) {
                System.out.println("  - class: " + allDivs.get(i).getAttribute("class"));
            }

            // 2. Все section элементы
            System.out.println("\n2. ВСЕ SECTION ЭЛЕМЕНТЫ:");
            System.out.println("-".repeat(100));
            List<WebElement> sections = driver.findElements(By.xpath("//section"));
            System.out.println("Найдено section: " + sections.size());
            for (int i = 0; i < Math.min(2, sections.size()); i++) {
                System.out.println("  - class: " + sections.get(i).getAttribute("class"));
                System.out.println("  - id: " + sections.get(i).getAttribute("id"));
            }

            // 3. Все h2, h3 заголовки (вопросы часто в заголовках)
            System.out.println("\n3. ЗАГОЛОВКИ (H1-H3) - ВОЗМОЖНЫЕ ВОПРОСЫ:");
            System.out.println("-".repeat(100));
            List<WebElement> headings = driver.findElements(By.xpath("//h2 | //h3"));
            System.out.println("Найдено заголовков: " + headings.size());
            for (int i = 0; i < Math.min(5, headings.size()); i++) {
                String text = headings.get(i).getText();
                if (!text.isEmpty()) {
                    System.out.println("  - [" + headings.get(i).getTagName() + "] " + text.substring(0, Math.min(60, text.length())));
                }
            }

            // 4. Все кнопки с текстом
            System.out.println("\n4. ВСЕ КНОПКИ:");
            System.out.println("-".repeat(100));
            List<WebElement> buttons = driver.findElements(By.xpath("//button"));
            System.out.println("Найдено кнопок: " + buttons.size());
            for (int i = 0; i < Math.min(10, buttons.size()); i++) {
                String text = buttons.get(i).getText();
                String class_attr = buttons.get(i).getAttribute("class");
                System.out.println("  [" + i + "] Текст: '" + text + "', class: " + class_attr);
            }

            // 5. Попробуем клик на первую кнопку "Заказать"
            System.out.println("\n5. ТЕСТ КЛИКА НА КНОПКУ 'Заказать':");
            System.out.println("-".repeat(100));
            List<WebElement> orderBtns = driver.findElements(By.xpath("//button[contains(text(), 'Заказать')]"));
            System.out.println("Найдено кнопок 'Заказать': " + orderBtns.size());
            if (orderBtns.size() > 0) {
                System.out.println("Кликаю на первую кнопку 'Заказать'...");
                orderBtns.get(0).click();
                Thread.sleep(2000);

                // Проверяем что открылось после клика
                System.out.println("\nПосле клика на 'Заказать':");
                
                // Ищем модальное окно
                List<WebElement> modals = driver.findElements(By.xpath("//div[contains(@class, 'odal') or contains(@class, 'Modal') or contains(@class, 'popup')]"));
                System.out.println("  Найдено модальных окон: " + modals.size());
                
                // Ищем инпуты
                List<WebElement> inputs = driver.findElements(By.xpath("//input"));
                System.out.println("  Найдено input элементов: " + inputs.size());
                
                if (inputs.size() > 0) {
                    System.out.println("  Первые 5 инпутов:");
                    for (int i = 0; i < Math.min(5, inputs.size()); i++) {
                        WebElement inp = inputs.get(i);
                        System.out.println("    [" + i + "] type=" + inp.getAttribute("type") + 
                                         ", placeholder=" + inp.getAttribute("placeholder") +
                                         ", name=" + inp.getAttribute("name") +
                                         ", id=" + inp.getAttribute("id"));
                    }
                }

                // Проверяем видимость всех input'ов
                System.out.println("\n  Проверка видимости инпутов:");
                for (int i = 0; i < Math.min(3, inputs.size()); i++) {
                    try {
                        boolean visible = inputs.get(i).isDisplayed();
                        System.out.println("    Input[" + i + "] видим: " + visible);
                    } catch (Exception e) {
                        System.out.println("    Input[" + i + "] - ошибка: " + e.getMessage());
                    }
                }
            }

            // 6. Вывод всего HTML (первые 3000 символов)
            System.out.println("\n6. СРЕЗ HTML СТРАНИЦЫ (первые 3000 символов):");
            System.out.println("-".repeat(100));
            String html = (String) ((JavascriptExecutor) driver).executeScript("return document.documentElement.outerHTML;");
            System.out.println(html.substring(0, Math.min(3000, html.length())));
            System.out.println("\n... [HTML обрезан] ...\n");

            System.out.println("=".repeat(100));
            System.out.println("ОТЛАДКА ЗАВЕРШЕНА");
            System.out.println("=".repeat(100));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
