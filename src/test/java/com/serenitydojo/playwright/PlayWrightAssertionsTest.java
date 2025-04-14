package com.serenitydojo.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class PlayWrightAssertionsTest {

    static Playwright playwright;
    static Browser browser;
    static BrowserContext browserContext;

    Page page;

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test");
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
        );
    }

    @BeforeEach
    void setUp() {
        browserContext = browser.newContext();
        page = browserContext.newPage();
        
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


    @DisplayName("Making assertions about a contents of the field")
    @Nested
    class LocatingElementsUsingCSS {

        @BeforeEach
        void openContactPage() {
            page.navigate("https://practicesoftwaretesting.com/");
            page.waitForCondition(() -> page.getByTestId("product-name").count() > 0);

        }

        @DisplayName("Check the value of the field")
        @Test
        void fieldValues() {
            page.navigate("https://practicesoftwaretesting.com/contact/"); 
            var firstNameField = page.getByLabel("First name");
            firstNameField.fill("Sarah Jane");
            assertThat(firstNameField).hasValue("Sarah Jane");
            assertThat(firstNameField).not().isDisabled();
            assertThat(firstNameField).isEditable();

        }

        @DisplayName("Making assertions about data values")
        @Test
        void allProductPricesShouldBeCorectValues() {
            List<Double> prices = page.getByTestId("product-price")
            .allInnerTexts()
            .stream()
            .map(price -> Double.parseDouble(price.replace("$","")))
            .toList();
        
            Assertions.assertThat(prices)
            .allMatch(price -> price > 0.0)
            .isNotEmpty()
            .doesNotContain(0.0)
            .allMatch(price -> price < 1000)
            .allSatisfy(price -> 
                Assertions.assertThat(price)
                    .isGreaterThan(0.0)
                    .isLessThan(1000.0));
        }

        @DisplayName("Sort in alphabetical order")
        @Test
        void sortInAlphabeticalOrder() {
            page.getByTestId("sort").selectOption("Name (A - Z)");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            List<String> productNames = page.getByTestId("product-name").allTextContents();

            Assertions.assertThat(productNames).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);

            Assertions.assertThat(productNames).isSortedAccordingTo(Comparator.naturalOrder());
        }

        @DisplayName("Sort in reverse alphabetical order")
        @Test
        void sortInReverseAlphabeticalOrder() {
            page.getByTestId("sort").selectOption("Name (Z - A)");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            List<String> productNames = page.getByTestId("product-name").allTextContents();

            Assertions.assertThat(productNames).isSortedAccordingTo(Comparator.reverseOrder());

            Assertions.assertThat(productNames).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER.reversed());
        }

        /*@DisplayName("Sort by price")
        @Test
        void sortByPrice() {
            page.getByTestId("sort").selectOption("price"")
        }*/


        
    }


}

