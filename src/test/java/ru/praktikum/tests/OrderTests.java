package ru.praktikum.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import ru.praktikum.pages.MainPage;
import ru.praktikum.pages.OrderPage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

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
    @MethodSource("orderData")
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
                () -> "Должно появиться сообщение об успешном создании заказа. " + orderPage.getOrderSuccessDiagnostics());
    }

    static Stream<Arguments> orderData() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String date1 = LocalDate.now().plusDays(1).format(formatter);
        String date2 = LocalDate.now().plusDays(2).format(formatter);

        return Stream.of(
                Arguments.of("Иван", "Иванов", "Москва", "+79991234567", date1, "сутки",
                        "Привезите быстрее", "TOP", "Сокольники", "BLACK"),
                Arguments.of("Петр", "Петров", "Москва", "+78125551234", date2, "сутки",
                        "Позвоните перед приездом", "BOTTOM", "Домодедовская", "GREY")
        );
    }
}