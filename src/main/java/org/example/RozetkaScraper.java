package org.example;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class RozetkaScraper {

    private static final String TILE = "rz-product-tile";
    private static final String URL = "https://rozetka.com.ua/ua/mobile-phones/c80003/";

    public static void main(String[] args) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--start-maximized");
        options.addArguments("--lang=uk-UA");
        options.setExperimentalOption("excludeSwitches", List.of("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--user-data-dir=" + System.getProperty("user.home") + "/rozetka-chrome-profile");

        Configuration.browserCapabilities = options;
        Configuration.browser = "chrome";
        Configuration.headless = false;
        Configuration.pageLoadStrategy = "eager";
        Configuration.pageLoadTimeout = 60000;

        hideAutomation();

        open(URL);
        sleep(rnd(1500, 3000));

        $$(TILE).shouldHave(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(120));

        ElementsCollection products = loadAllTiles();

        System.out.println("Total products: " + products.size());
        System.out.println("=== ALL DEVICES ===");
        for (SelenideElement product : products) {
            System.out.println(getName(product) + " | Price: " + getPrice(product));
        }

        System.out.println("\n=== DEVICES LABELED 'TOP SALES' ===");
        for (SelenideElement product : products) {
            if (product.getText().toLowerCase().contains("топ продаж")) {
                System.out.println(getName(product) + " | Price: " + getPrice(product));
            }
        }

        closeWebDriver();
    }

    private static void hideAutomation() {
        open("about:blank");
        ChromeDriver driver = (ChromeDriver) getWebDriver();
        String script =
                "Object.defineProperty(navigator, 'webdriver', { get: () => undefined });" +
                "Object.defineProperty(navigator, 'languages', { get: () => ['uk-UA', 'uk', 'ru', 'en-US'] });";
        driver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", Map.of("source", script));
    }

    private static ElementsCollection loadAllTiles() {
        int count = $$(TILE).size();
        while (true) {
            scrollLikeHuman();
            if (!waitForMoreTiles(count)) {
                break;
            }
            count = $$(TILE).size();
        }
        return $$(TILE);
    }

    private static void scrollLikeHuman() {
        while (!isAtBottom()) {
            executeJavaScript("window.scrollBy(0, arguments[0])", rnd(400, 800));
            sleep(rnd(120, 350));
        }
    }

    private static boolean isAtBottom() {
        Boolean atBottom = executeJavaScript(
                "return window.innerHeight + window.scrollY >= document.body.scrollHeight - 5");
        return Boolean.TRUE.equals(atBottom);
    }

    private static boolean waitForMoreTiles(int currentCount) {
        try {
            Wait().withTimeout(Duration.ofSeconds(3))
                    .until(driver -> $$(TILE).size() > currentCount);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    private static int rnd(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private static String getName(SelenideElement tile) {
        SelenideElement title = tile.$("a.tile-title");
        if (!title.exists()) return "—";
        String text = title.getText().trim();
        return text.isEmpty() ? String.valueOf(title.getAttribute("title")) : text;
    }

    private static String getPrice(SelenideElement tile) {
        SelenideElement price = tile.$(".price");
        if (!price.exists()) return "—";
        return price.getText().replace('\u00A0', ' ').trim();
    }
}
