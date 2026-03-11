package ru.praktikum.tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.praktikum.pages.MainPage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FaqTests extends BaseTest {

    private MainPage mainPage;

    // Ожидаемые тексты вопросов и ответов из блока "Вопросы о важном"
    private static final String[] EXPECTED_QUESTIONS = {
            "Сколько это стоит? И как оплатить?",
            "Хочу сразу несколько самокатов! Так можно?",
            "Как рассчитывается время аренды?",
            "Можно ли заказать самокат прямо на сегодня?",
            "Можно ли продлить заказ или вернуть самокат раньше?",
            "Вы привозите зарядку вместе с самокатом?",
            "Можно ли отменить заказ?",
            "Я живу за МКАДом, привезёте?"
    };

    private static final String[] EXPECTED_ANSWERS_SUBSTRINGS = {
            "Сутки — 400 рублей. Оплата курьеру — наличными или картой.",
            "Пока что у нас так: один заказ — один самокат. Если хотите покататься с друзьями, можете просто сделать несколько заказов — один за другим.",
            "Допустим, вы оформляете заказ на 8 мая. Мы привозим самокат 8 мая в течение дня. Отсчёт времени аренды начинается с момента, когда вы оплатите заказ курьеру. Если мы привезли самокат 8 мая в 20:30, суточная аренда закончится 9 мая в 20:30.",
            "Только начиная с завтрашнего дня. Но скоро станем расторопнее.",
            "Пока что нет! Но если что-то срочное — всегда можно позвонить в поддержку по красивому номеру 1010.",
            "Самокат приезжает к вам с полной зарядкой. Этого хватает на восемь суток — даже если будете кататься без передышек и во сне. Зарядка не понадобится.",
            "Да, пока самокат не привезли. Штрафа не будет, объяснительной записки тоже не попросим. Все же свои.",
            "Да, обязательно. Всем самокатов! И Москве, и Московской области."
    };

    @BeforeEach
    public void setUp() {
        setUpDriver();
        mainPage = new MainPage(driver);
        mainPage.navigate();
    }

    @ParameterizedTest(name = "Вопрос #{0}")
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7})
    public void testFaqAccordionOpensCorrectAnswer(int questionIndex) {
        mainPage.clickQuestionButton(questionIndex);
        // Проверяем, что вопрос содержит ожидаемый текст
        String actualQuestion = mainPage.getQuestionText(questionIndex);
        assertEquals(EXPECTED_QUESTIONS[questionIndex], actualQuestion,
                "Текст вопроса должен совпадать с ожидаемым");

        // Проверяем, что открылся и отображается ответ
        assertTrue(mainPage.isAnswerVisible(questionIndex),
                "Ответ на вопрос номер " + (questionIndex + 1) + " должен быть виден");

        // Проверяем, что ответ содержит ожидаемый фрагмент текста
        String answerText = mainPage.getAnswerText(questionIndex);
        assertTrue(answerText.contains(EXPECTED_ANSWERS_SUBSTRINGS[questionIndex]),
                "Текст ответа должен содержать ожидаемое описание");
    }

}