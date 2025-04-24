package com.serenitydojo.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

public class AddingItemsToTheCartTest {

    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;

Page page;

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
        );
        playwright.selectors().setTestIdAttribute("data-test");
    }

    @BeforeEach
    void setUp() {
        browserContext = browser.newContext();
        page = browserContext.newPage();
        page.setViewportSize(1920, 1080);
        page.navigate("https://practicesoftwaretesting.com/");
    }

    @AfterEach
    void closeContext() {
        browserContext.close();
    }

    @AfterAll
    static void tearDown() {
        browser.close();
        playwright.close();
    }

    @DisplayName("Search for Pliers")
    @Test
    void searchForPliers(){
        page.getByPlaceholder("Search").fill("Pliers");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();

        List<String> products = page.getByTestId("product-name").allTextContents();
        Assertions.assertThat(products.get(0)).containsIgnoringCase("Pliers");

        int cardCount = page.locator(".card").count();
        Assertions.assertThat(cardCount).isEqualTo(9);

        
        List<String> productNames = page.getByTestId("product-name").allTextContents();
        Assertions.assertThat(productNames).allMatch(name -> name.contains("Pliers"));

        Locator outOfStockItem = page.locator(".card")
            .filter(new Locator.FilterOptions().setHasText("Out of stock"))
            .getByTestId("product-name");

            
        Assertions.assertThat(outOfStockItem).isEqualTo(3);
        
    }

  


    }