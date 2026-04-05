package com.expense.automation.support;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;

public final class AutomationSupport {

	private static volatile String lastRegisteredEmail;
	private static volatile String lastRegisteredPassword;

	private AutomationSupport() {
	}

	/** Called after a successful registration so {@link #resolveLoginCredentials()} can sign in without env vars. */
	public static void setLastRegisteredAccount(String email, String password) {
		lastRegisteredEmail = email;
		lastRegisteredPassword = password;
	}

	/**
	 * Explicit {@code test.user.email} / {@code test.user.password} (or env) take precedence; otherwise uses the
	 * account created in the same JVM run by {@code RegisterTest}.
	 */
	public static String[] resolveLoginCredentials() {
		String email = firstNonBlank(System.getProperty("test.user.email"), System.getenv("TEST_USER_EMAIL"));
		String password = firstNonBlank(System.getProperty("test.user.password"), System.getenv("TEST_USER_PASSWORD"));
		if (email != null && password != null) {
			return new String[] { email, password };
		}
		if (lastRegisteredEmail != null && lastRegisteredPassword != null) {
			return new String[] { lastRegisteredEmail, lastRegisteredPassword };
		}
		return null;
	}

	public static String resolveBaseUrl() {
		String baseUrl = firstNonBlank(System.getProperty("app.base.url"), System.getenv("APP_BASE_URL"));
		if (baseUrl == null) {
			baseUrl = "http://localhost:5173";
		}
		return baseUrl.replaceAll("/$", "");
	}

	public static String defaultPassword() {
		String p = firstNonBlank(System.getProperty("test.user.password"), System.getenv("TEST_USER_PASSWORD"));
		return p != null ? p : "Autotest1";
	}

	public static void assertSignedInShell(WebDriver driver) {
		Assert.assertTrue(
				driver.getPageSource().contains("Money Mgr") && driver.getPageSource().contains("Logout"),
				"Expected authenticated app shell");
	}

	public static WebDriver newChromeDriver() {
		WebDriverManager.chromedriver().setup();
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--window-size=1920,1080");
		return new ChromeDriver(options);
	}

	public static String firstNonBlank(String a, String b) {
		if (a != null && !a.isBlank()) {
			return a.trim();
		}
		if (b != null && !b.isBlank()) {
			return b.trim();
		}
		return null;
	}
}
