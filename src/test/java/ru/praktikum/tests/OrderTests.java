package ru.praktikum.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.praktikum.pages.MainPage;
import ru.praktikum.pages.OrderPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderTests extends BaseTest {

    private MainPage mainPage;
    private OrderPage orderPage;

    @BeforeEach
    public void setUp() {
        setUpDriver();

        mainPage = new MainPage(driver);
        orderPage = new OrderPage(driver);
        mainPage.navigate();
    }

    @DisplayName("Позитивный заказ самоката через разные кнопки")
    @ParameterizedTest(name = "Кнопка: {7}, пользователь: {0} {1}")
    @CsvSource({
            "Иван,Иванов,Москва,+79991234567,16.12.2025,сутки,Привезите быстрее,TOP,Сокольники,BLACK",
            "Петр,Петров,Москва,+78125551234,17.12.2025,сутки,Позвоните перед приездом,BOTTOM,Домодедовская,GREY"
    })
    public void testOrderFlowFromDifferentButtons(
            String name,
            String surname,
            String address,
            String phone,
            String date,
            String duration,
            String comment,
            String entryPoint,
            String metroStation,
            String color
    ) {

        if ("TOP".equalsIgnoreCase(entryPoint)) {
            mainPage.clickOrderButtonTop();
        } else {
            mainPage.clickOrderButtonBottom();
        }

        orderPage.fillFirstPart(name, surname, address, metroStation, phone);
        orderPage.clickNextButton();

        orderPage.selectDeliveryDate(date);
        orderPage.selectRentalDuration(duration);

        if ("BLACK".equalsIgnoreCase(color)) {
            orderPage.selectBlackColor();
        } else {
            orderPage.selectGreyColor();
        }

        orderPage.fillComment(comment);
        orderPage.clickOrderButton();

        assertTrue(orderPage.isSuccessMessageDisplayed(),
                "Должно появиться сообщение об успешном создании заказа");
    }

}