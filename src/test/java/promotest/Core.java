package promotest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.WebDriver;

import java.lang.Thread;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


public class Core {

    // Объявление переменных ссылок и локаторов элементов
    public static WebDriver driver;
    private static String baseUrl = "https://www.ivi.ru";
    private static String watchLatterButtonActiveState = "huge action-link bright js-favourite-button favorite active";
    private static String watchLatterButtonXPathLocator = "//li[6]/div/a[3]";
    private static String arrowNextButtonCssLocator = "div.promo span.control.next";
    private static String watchLatterButtonTextCssLocator = "#favourites-title > a:nth-child(1)";
    private static String watchLatterMovieContainerCssLocator = "#favourites > li:nth-child(1)";
    private static String watchLatterMovieLinkContainerCssLocator = "#favourites > li:nth-child(1) > a:nth-child(1)";
    private static String expectedMovieLink = "https://www.ivi.ru/watch/ne-plach-ya-uhozhu";


    static void setup() {
        // Иногда кнопки не нажимаются, поэтому сделал запуск браузера в полном экране.
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);

    }

    static void loadMainPage() {

        /* Столкнулся с такой проблемой: сайт открывается, но очень долго подгружаются какие-то процессы ( api.ivi.ru, dfs.ivi.ru, итд)
        драйвер не выполняет следующие инструкции пока на завершится загрузка страницы. Если руками нажать ESC то сценарий продолжит выполнение,
        но программно послать, например .sendKeys("Keys.ESCAPE") не получается по той же причине.
        Нашёл решение через запуск потока, вызывается таймер (который через 6 секунд останавливает загрузку страницы), сразу после запуска начинается
        загрузка страницы, через 6 секунд загрузка останавливается.
        */
        TimeoutThread timeoutThread = new TimeoutThread(6);
        timeoutThread.start(); // запуск таймера

        driver.get(baseUrl); // запуск браузера
        timeoutThread.interrupt(); // остановка потока

        Throwable e = ThreadReturn.get(timeoutThread.getName());
        if (e != null) {
            System.out.println("Timed out: " + e.getMessage());
        } else {
            System.out.println("No timeout");
        }

        System.out.println("Страница загружена");
    }


    static void slidePromo(int count) throws InterruptedException {
        WebElement arrowNext = driver.findElement(By.cssSelector(arrowNextButtonCssLocator));
        System.out.println(arrowNext.getText());

        // Цикл для реализации пяти нажатий кнопки промо-слайдера
        for (int i = 0; i < count; i++) {
            arrowNext.click();
            Thread.sleep(500);
        }
    }

    public static void clickWatchLatter() throws InterruptedException {
        // Нажать кнопку "Смотреть позже"
        WebElement watchLatter = driver.findElement(By.xpath(watchLatterButtonXPathLocator));
        watchLatter.click();
        String clickedMovieID = watchLatter.getAttribute("data-object-id");
        Thread.sleep(2000);
        System.out.println(clickedMovieID);

    }

    static void checkWatchLatterButton() throws InterruptedException {
        // Не нашёл как проверить что звезда стала "красной", поэтому проверяю, что класс кнопки изменился на *active
        WebElement watchLatter = driver.findElement(By.xpath(watchLatterButtonXPathLocator));
        assertThat(watchLatter.getAttribute("class"), is(watchLatterButtonActiveState));


    }

    static void checkWatchLatterBlock() throws InterruptedException {

        // Почему-то в "чистом" браузере кнопка "Смотреть позже" динамически не меняется
        // и не появляется блок с фильмом добавленным в "Смотерть позже" поэтому пришлось обновлять страницу
        driver.navigate().refresh();
        Thread.sleep(3000);

        //Сравнить ссылки на фильм из блока промо и блока См. позже
        String watchLatterLink = driver.findElement(By.cssSelector(watchLatterMovieLinkContainerCssLocator)).getAttribute("href");
        String clickedMovieID = driver.findElement(By.xpath(watchLatterButtonXPathLocator)).getAttribute("data-object-id");
        assertThat(watchLatterLink, is(expectedMovieLink));

        // Получение текста  и проверка заголовка появившегося блока "Смотреть позже"
        String watchLatterButtonText = driver.findElement(By.cssSelector(watchLatterButtonTextCssLocator)).getText();
        System.out.println(watchLatterButtonText);

        String selectedMovieID = driver.findElement(By.cssSelector(watchLatterMovieContainerCssLocator)).getAttribute("data-id");
        System.out.println(clickedMovieID);
        assertThat(watchLatterButtonText, is("См. позже"));

        // Проверка того, что совпадают ID фильмов нажатой кнопки "Смотреть позже" и добавленного в блок под слайдером
        assertThat(selectedMovieID,is(clickedMovieID));
        System.out.println("Всё верно, в раздел \"Смотреть позже\" добавлен правильный фильм.");

    }

    static void quit() {
        driver.close();
        System.exit(0);
    }


}