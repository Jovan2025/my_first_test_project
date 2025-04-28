package com.serenitydojo.playwright;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.playwright.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Execution(ExecutionMode.SAME_THREAD)

public class PlaywrightRestAPITest {
        protected static Playwright playwright;
        protected static Browser browser;
        protected static BrowserContext browserContext;

        Page page;

        @BeforeAll
        static void setUpBrowser() {
            playwright = Playwright.create();
            playwright.selectors().setTestIdAttribute("data-test");
            browser = playwright.chromium().launch
            (new BrowserType.LaunchOptions().setHeadless(false).setArgs
            (Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
            );

            } 
        
            @BeforeEach
            void setUp() {
                browserContext = browser.newContext();
                page = browserContext.newPage();

                page.navigate("https://practicesoftwaretesting.com");
                page.getByPlaceholder("Search").waitFor();
            }

            @AfterEach
            void closeContext() {
                browser.close();
                playwright.close();
            }

            @DisplayName("Mocking out API responses")
            @Nested
            class MockingAPIResponses {

                @Test
                @DisplayName("When a search returns a single product")
                void whenASingleItemIsFound() {

                    // Mock the API response for the search query "Pliers"
                    
                    page.route("**/products/search?q=pliers", 
                    route -> route.fulfill(new Route.FulfillOptions()
                    .setBody(MockSearchResponses.RESPONSE_WITH_A_SINGLE_ENTRY)
                    .setStatus(200))
                    ); 
                
                    var searchBox = page.getByPlaceholder("Search");
                    searchBox.fill("Pliers");
                    searchBox.press("Enter");

                    assertThat(page.getByTestId("product-name").first()).hasText("Super Pliers");
                    assertThat(page.getByTestId("product-name")).hasCount(1);


                }
            }

}
