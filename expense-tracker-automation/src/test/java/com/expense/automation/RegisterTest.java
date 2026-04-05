package com.expense.automation;

import com.expense.automation.support.AutomationSupport;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.time.Duration;

public class RegisterTest {

	@Test
	public void userCanCreateAccount() {
		String baseUrl = AutomationSupport.resolveBaseUrl();
		String password = AutomationSupport.defaultPassword();
		String email = "e2e." + System.currentTimeMillis() + "@example.com";
		String fullName = "E2E User";

		WebDriver driver = AutomationSupport.newChromeDriver();

		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

			driver.get(baseUrl + "/register");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='name']")));
			driver.findElement(By.cssSelector("input[autocomplete='name']")).sendKeys(fullName);
			driver.findElement(By.cssSelector("input[autocomplete='email']")).sendKeys(email);
			driver.findElement(By.cssSelector("input[autocomplete='new-password']")).sendKeys(password);
			driver.findElement(By.xpath("//button[@type='submit' and contains(., 'Register')]")).click();

			wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/register")));
			AutomationSupport.assertSignedInShell(driver);
			AutomationSupport.setLastRegisteredAccount(email, password);
		} finally {
			driver.quit();
		}
	}
}
