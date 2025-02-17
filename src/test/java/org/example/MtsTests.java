package com.lesson14.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.List;

public class MtsTests {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().driverVersion("133.0.6943.99").setup();
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get("https://www.mts.by");
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testBlockTitle() {
        WebElement blockTitle = driver.findElement(By.xpath("//*[contains(text(), 'Онлайн пополнение без комиссии')]"));
        assertNotNull(blockTitle);
        assertTrue(blockTitle.getText().contains("Онлайн пополнение без комиссии"));
    }

    @Test
    public void testPaymentLogosPresence() {
        List<WebElement> logos = driver.findElements(By.xpath("//div[contains(@class,'payment-logos')]//img"));
        assertFalse(logos.isEmpty());
        for (WebElement logo : logos) {
            assertTrue(logo.isDisplayed());
        }
    }

    @Test
    public void testServiceLink() {
        WebElement detailsLink = driver.findElement(By.linkText("Подробнее о сервисе"));
        assertNotNull(detailsLink);
        String originalWindow = driver.getWindowHandle();
        detailsLink.click();
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                driver.switchTo().window(windowHandle);
                break;
            }
        }
        String newUrl = driver.getCurrentUrl();
        assertTrue(newUrl.contains("service") || newUrl.contains("подробн"));
        driver.close();
        driver.switchTo().window(originalWindow);
    }

    @Test
    public void testContinueButton() {
        WebElement servicesRadio = driver.findElement(By.xpath("//label[contains(text(),'Услуги связи')]/preceding-sibling::input[@type='radio']"));
        servicesRadio.click();
        WebElement phoneInput = driver.findElement(By.xpath("//input[@type='tel']"));
        phoneInput.clear();
        phoneInput.sendKeys("297777777");
        WebElement continueButton = driver.findElement(By.xpath("//button[contains(text(),'Продолжить')]"));
        continueButton.click();
        WebElement confirmation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Подтверждение')]")));
        assertTrue(confirmation.isDisplayed());
    }
}
