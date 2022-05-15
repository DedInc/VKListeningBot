package com.github.dedinc.vklisteningbot;

import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebElement;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import com.github.dedinc.vklisteningbot.utils.Downloader;
import com.github.dedinc.vklisteningbot.utils.SeleniumUtils;
import com.github.dedinc.vklisteningbot.utils.UserAgents;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;

public class Main {
	
	public static String cd = System.getenv("systemDrive");
	public static String username = System.getenv("USERNAME");
	
	public static void main(String[] args) {
        Options options = new Options();

        Option login = new Option("l", "login", true, "login VK");
        login.setRequired(true);
        options.addOption(login);

        Option password = new Option("p", "pass", true, "password VK");
        password.setRequired(true);
        options.addOption(password);
		
        Option link = new Option("link", true, "link of Album/Playlist");
        link.setRequired(true);
        options.addOption(link);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("VKListeningBot", options);
            System.exit(1);
        }

        String log = cmd.getOptionValue("login");
        String pass = cmd.getOptionValue("pass");
        String albumLink = cmd.getOptionValue("link");
        
		System.setProperty("webdriver.chrome.driver", SeleniumUtils.getChromeDriver(false));
		ChromeOptions coptions = new ChromeOptions();
		final String[] arguments = {"notifications", "images", "plugins", "popups"};
		Map<String, Object> pf = new HashMap<String, Object>();
		for (String arg : arguments) {
			pf.put("profile.default_content_setting_values." + arg, 2);
		}
		coptions.addArguments("--headless");
		coptions.addArguments("--disable-infobars");
		coptions.addArguments("--disable-extensions");
		coptions.addArguments("--user-agent=" + UserAgents.getAgent());
		coptions.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
		coptions.setExperimentalOption("prefs", pf);
		ChromeDriver driver;
		try {
			driver = new ChromeDriver(coptions);
		} catch (SessionNotCreatedException e) {
			e.printStackTrace();
			System.out.println("Driver outdated! Updating...");
			System.setProperty("webdriver.chrome.driver", SeleniumUtils.getChromeDriver(true));
			System.out.println("Driver has been updated!");
			driver = new ChromeDriver(coptions);
		}
		
		System.out.println("Auth...");
		driver.get("https://vk.com/login");
		
		WebElement loginButton = driver.findElementByXPath("/html/body/div[10]/div/div/div[2]/div[2]/div[2]/div/div/div/div[3]/button[1]");
		loginButton.click();
		WebElement email = driver.findElementByName("login");
		WebElement nextButton = driver.findElementByXPath("/html/body/div/div/div/div[2]/div/form/div[2]/div[1]/button");
		email.sendKeys(log);
		nextButton.click();
		try {
			Thread.sleep(500);
		   } catch (Exception e) {
		}
		WebElement passElem = driver.findElementByName("password");
		nextButton = driver.findElementByXPath("/html/body/div/div/div/div[2]/div/form/div[2]/button");
		passElem.sendKeys(pass);
		nextButton.click();
		while (!waitElement(driver, "xpath", "/html/body/div[11]/div/div/div[2]/div[1]/div/nav/ol/li[7]/a", 8)) {
			try {
				driver.findElementByClassName("vkc__TextField__error");
				System.out.println("Wrong login and pass!");
				driver.quit();
				System.exit(1);
			   } catch (Exception e) {
			}
			try {
				driver.findElementByClassName("vkc__Captcha__image");
				System.out.println("Captcha detected! Load captcha.png...");
				Downloader.download(driver.findElementByXPath("/html/body/div/div/div[2]/div/form/img").getAttribute("src"), Paths.get(System.getProperty("user.dir"), "captcha.png").toString());
			    Scanner scanner = new Scanner(System.in);
			    System.out.println("Loaded!\nEnter a captcha: ");
			    String captcha = scanner.nextLine();
			    driver.findElementByXPath("/html/body/div/div/div[2]/div/form/div[1]/div/div/input").sendKeys(captcha);
			    driver.findElementByXPath("/html/body/div/div/div[2]/div/form/div[2]/button").click();			    
			   } catch (Exception e) {
			}
		}
		System.out.println("Logged!");
		driver.get(albumLink);
		
		System.out.println("Listening...");
		int tracks = 0;
		int listenings = 0;
		for (int i = 1; i != Integer.MAX_VALUE; i++) {
			try {
				driver.findElementByXPath(String.format("/html/body/div[11]/div/div/div[2]/div[2]/div[2]/div/div/div/div/div/div[2]/div/div[2]/div[2]/div/div[%d]/div/div[6]/div[2]", i));
				tracks += 1;
			} catch (Exception e) {
				break;
			}
		}
		
		int prev = 0;
		while (true) {
			int k = 1;
			while (true) {
				k = (int) (Math.random()*tracks);
				if (k != 0 && k != prev) {
					break;
				}
			}
			Actions action = new Actions(driver);
			WebElement track = driver.findElementByXPath(String.format("/html/body/div[11]/div/div/div[2]/div[2]/div[2]/div/div/div/div/div/div[2]/div/div[2]/div[2]/div/div[%d]/div/button", k));
			String duration = driver.findElementByXPath(String.format("/html/body/div[11]/div/div/div[2]/div[2]/div[2]/div/div/div/div/div/div[2]/div/div[2]/div[2]/div/div[%d]/div/div[6]/div[3]/div", k)).getText();
			int listenTime = (Integer.parseInt(duration.split(":")[0]) * 60 + Integer.parseInt(duration.split(":")[1])) / 2;
			action.moveToElement(track).click().perform();
			prev = k;
			try {
				Thread.sleep(listenTime * 1000);
			   } catch (Exception e) {
			}
			if (System.getProperty("os.name").toLowerCase().contains("win")) {
				try {
					Runtime.getRuntime().exec("cls");
				   } catch (Exception e) {
				}
			} else {
				try {
					Runtime.getRuntime().exec("clear");
				   } catch (Exception e) {
				}
			}
			listenings += 1;
			System.out.println("Listenings: " + listenings);
		}
	}
	
	public static boolean waitElement(ChromeDriver driver, String type, String arg, int maxTime) {
		int s = 0;
		while (s != maxTime) {
			try {
				if (type.equalsIgnoreCase("id")) {
					driver.findElementById(arg);
					return true;
				} else if (type.equalsIgnoreCase("classname")) {
					driver.findElementByClassName(arg);
					return true;
				} else if (type.equalsIgnoreCase("xpath")) {
					driver.findElementByXPath(arg);
					return true;
				}
			} catch (Exception e) {
				try {
					Thread.sleep(1000);
					s += 1;
			     	} catch (Exception ee) {
				 }
			}
		}
		return false;
	}
}
