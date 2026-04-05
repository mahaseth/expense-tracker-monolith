package com.expense.automation;

import com.expense.automation.support.AutomationSupport;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.time.Duration;

public class LoginTest {

	@Test
	public void validLoginTest() {
		String baseUrl = AutomationSupport.resolveBaseUrl();
		String[] creds = AutomationSupport.resolveLoginCredentials();
		if (creds == null) {
			throw new SkipException(
					"Run RegisterTest first (full TestNG suite), or set test.user.email and test.user.password "
							+ "(or TEST_USER_EMAIL / TEST_USER_PASSWORD).");
		}
		String email = creds[0];
		String password = creds[1];

		WebDriver driver = AutomationSupport.newChromeDriver();

		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

			driver.get(baseUrl + "/login");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[autocomplete='email']")));
			driver.findElement(By.cssSelector("input[autocomplete='email']")).sendKeys(email);
			driver.findElement(By.cssSelector("input[autocomplete='current-password']")).sendKeys(password);
			driver.findElement(By.xpath("//button[@type='submit' and contains(., 'Sign in')]")).click();

			wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
			AutomationSupport.assertSignedInShell(driver);
		} finally {
			driver.quit();
		}
	}
}
