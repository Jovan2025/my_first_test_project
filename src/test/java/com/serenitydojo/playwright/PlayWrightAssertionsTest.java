package com.serenitydojo.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
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
        page.setViewportSize(1920, 1080);
        
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

        @DisplayName("Verify all product images are loaded on each page")
        @Test
        void allProductImagesShouldBeLoaded() {
            // Navigate to the home page
            page.navigate("https://practicesoftwaretesting.com/");
            page.waitForLoadState(LoadState.NETWORKIDLE);

            // Loop through all pages
            while (true) {
                // Check if all product images are loaded on the current page
                @SuppressWarnings("unchecked")
                List<Boolean> imageStatuses = (List<Boolean>) page.locator("img.card-img-top")
                    .evaluateAll("imgs => imgs.map(img => img.complete && img.naturalHeight > 0)");

                // Assert that all images are loaded
                Assertions.assertThat(imageStatuses)
                    .isNotEmpty()
                    .allMatch(status -> status);

            // Check if there is a "Next" button to navigate to the next page

            Locator nextButton = page.locator("li.page-item").last();
        

            // Check if the class contains "disabled" before clicking
            boolean isDisabled = (Boolean) nextButton.evaluate(
                "element => element.classList.contains('disabled')");

            if (isDisabled) {
                // If the "Next" button is disabled, break the loop
                System.out.println("No more pages to navigate.");
                break; // Exit the loop if the element is disabled
            }

            // Click the "Next" button to go to the next page
            nextButton.click();
            page.waitForLoadState(LoadState.NETWORKIDLE);}
                
            }

        
            @DisplayName("Verify if validation errors are visible on Login page")
            @Test

            void checkValidationErrorsOnLoginForm() {
                // Navigate to the home page
                page.navigate("https://practicesoftwaretesting.com/auth/login");
                page.waitForLoadState(LoadState.NETWORKIDLE);

                // Check if validation errors are visible on mandatory fields
                page.getByTestId("login-submit").click();
                assertThat(page.getByTestId("email-error")).isVisible();
                assertThat(page.getByTestId("password-error")).isVisible();
                assertThat(page.getByTestId("email-error")).hasText("Email is required");
                assertThat(page.getByTestId("password-error")).hasText("Password is required");

                page.getByTestId("email").fill("aleksicjovan@hotmail.com");
                page.getByTestId("password").fill("1234567890");
                page.getByTestId("login-submit").click();

                assertThat(page.getByTestId("email-error")).isHidden();
                assertThat(page.getByTestId("password-error")).isHidden();
                assertThat(page.getByTestId("login-submit")).isEnabled();
                assertThat(page.getByTestId("login-submit")).isVisible();
                assertThat(page.getByTestId("login-submit")).hasText("Login");
                assertThat(page.getByTestId("login-submit")).hasAttribute("type", "submit");
                assertThat(page.getByTestId("login-error")).isVisible();
                assertThat(page.getByTestId("login-error")).hasText("Invalid email or password");

            }

            @DisplayName("Verify if validation errors are visible on Registration page")
            @Test
            void checkValidationErrorsOnRegistrationForm() {
                // Navigate to the home page
                page.navigate("https://practicesoftwaretesting.com/auth/register");
                page.waitForLoadState(LoadState.NETWORKIDLE);

                // Check if validation errors are visible on mandatory fields
                page.getByTestId("register-submit").click();
                assertThat(page.getByTestId("first-name-error")).isVisible();
                assertThat(page.getByTestId("last-name-error")).isVisible();
                assertThat(page.getByTestId("dob-error")).isVisible();
                assertThat(page.getByTestId("street-error")).isVisible();
                assertThat(page.getByTestId("postal_code-error")).isVisible();
                assertThat(page.getByTestId("city-error")).isVisible();
                assertThat(page.getByTestId("state-error")).isVisible();
                assertThat(page.getByTestId("country-error")).isVisible();
                assertThat(page.getByTestId("phone-error")).isVisible();
                assertThat(page.getByTestId("email-error")).isVisible();
                assertThat(page.getByTestId("password-error")).isVisible();

                
                assertThat(page.getByTestId("first-name-error")).hasText("First name is required");
                //assertThat(page.getByTestId("last-name-error")).hasText("Last name is required");
                assertThat(page.getByTestId("dob-error")).hasText("Date of Birth is required");
                assertThat(page.getByTestId("street-error")).hasText("Street is required");
                assertThat(page.getByTestId("postal_code-error")).hasText("Postcode is required");
                assertThat(page.getByTestId("city-error")).hasText("City is required");
                assertThat(page.getByTestId("state-error")).hasText("State is required");
                assertThat(page.getByTestId("country-error")).hasText("Country is required");
                assertThat(page.getByTestId("phone-error")).hasText("Phone is required.");
                assertThat(page.getByTestId("email-error")).hasText("Email is required");
                assertThat(page.getByTestId("password-error")).hasText("Password is required  Password must be minimal 6 characters long.  Password can not include invalid characters.");


            }

            @DisplayName("Register new user")
            @Test

            void registerNewUser() {
                // Navigate to the home page
                page.navigate("https://practicesoftwaretesting.com/auth/register");
                page.waitForLoadState(LoadState.NETWORKIDLE);

                // Fill in the registration form
                page.getByTestId("first-name").fill("Jovan");
                page.getByTestId("last-name").fill("Aleksic");
                page.getByTestId("dob").fill("1986-03-24");
                page.getByTestId("street").fill("Street 123");
                page.getByTestId("postal_code").fill("12345");
                page.getByTestId("city").fill("Kovacica");
                page.getByTestId("state").fill("Vojvodina");
                page.getByTestId("country").selectOption("Serbia");
                page.getByTestId("phone").fill("1234567890");
                page.getByTestId("email").fill("aleksic.jovan.86@gmail.com");
                page.getByTestId("password").fill("Aa234567890$");
                page.getByTestId("register-submit").click();
            
            }


       
        }
    }


