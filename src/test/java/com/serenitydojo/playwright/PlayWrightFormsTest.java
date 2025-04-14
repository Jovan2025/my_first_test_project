package com.serenitydojo.playwright;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;

@UsePlaywright(HeadlessChromeOptions.class)

public class PlayWrightFormsTest {
    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeEach
    void openContactPage(Page page) {
        this.page = page;
        page.navigate("https://practicesoftwaretesting.com/contact");
    }
    
    @Nested
    class WhenInteractingWithTextFields {
       
    @DisplayName("Complete the form")
    @Test
        void completeForm(Page page) throws URISyntaxException {
            var firstNameField = page.getByLabel("First name");
            var lastNameField = page.getByLabel("last name");
            var emailNameField = page.getByLabel("Email");
            var messageField = page.getByLabel("Message");
            var subjectField = page.getByLabel("Subject");
            var uploadField = page.getByLabel("Attachment");
    

            firstNameField.fill("Sarah Jane");
            lastNameField.fill("Smith");
            emailNameField.fill("sarah-jane@example.com");
            messageField.fill("This is a test message.");
            subjectField.selectOption("Return");


        Path fileToUpload = Paths.get("C:\\Users\\jovan1.aleksic\\OneDrive - Synechron Inc\\Desktop\\Playwright\\my_first_test_project\\my_first_test_project\\src\\test\\java\\com\\serenitydojo\\data\\simple-text.txt");
        page.setInputFiles("#attachment", fileToUpload);

        assertThat(firstNameField).hasValue("Sarah Jane");
        assertThat(lastNameField).hasValue("Smith");
        assertThat(emailNameField).hasValue("sarah-jane@example.com");
        assertThat(messageField).hasValue("This is a test message.");
        assertThat(subjectField).hasValue("return");

        String uploadedFile = uploadField.inputValue();
        org.assertj.core.api.Assertions.assertThat(uploadedFile).endsWith("simple-text.txt");    

        
        }

        @DisplayName("Mandatory fields")
        @ParameterizedTest
        @ValueSource(strings = { "First name", "last name", "Email", "Message" })
        void mandatoryFields(String fieldName) {
            var firstNameField = page.getByLabel("First name");
            var lastNameField = page.getByLabel("last name");
            var emailNameField = page.getByLabel("Email");
            var messageField = page.getByLabel("Message");
            var subjectField = page.getByLabel("Subject");
            var sendButton = page.getByText("Send");

            
            //fill in the field values

            firstNameField.fill("Sarah Jane");
            lastNameField.fill("Smith");
            emailNameField.fill("sarah-jane@example.com");
            messageField.fill("This is a test message.");
            subjectField.selectOption("Return");

            //clear the field values

            var field = page.getByLabel(fieldName);
            assertThat(field).isVisible();
            field.clear();

            sendButton.click();


            var errorMessage = page.getByRole(AriaRole.ALERT).getByText(fieldName + " is required");

            //check the error message for that field
            assertThat(errorMessage).isVisible();
        
                }
            } // Close WhenInteractingWithTextFields class
        } // Close PlayWrightFormsTest class
