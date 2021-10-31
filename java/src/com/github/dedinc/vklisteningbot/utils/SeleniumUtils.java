package com.github.dedinc.vklisteningbot.utils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Scanner;
import com.github.dedinc.vklisteningbot.Main;

public class SeleniumUtils {
	
	public static String getChromeVersion() {		
		String chrome = Paths.get(Main.cd, "Users", Main.username, "AppData", "Local", "Google", "Chrome").toString();				
		String[] paths = {Paths.get(Main.cd, "Program Files", "Google", "Chrome", "Application").toString(), Paths.get(Main.cd, "Program Files (x86)", "Google", "Chrome", "Application").toString(),  Paths.get(chrome, "Application").toString()};
		for (int i = 0; i != paths.length; i++) {
			if (new File(paths[i]).exists()) {
				File[] files = new File(paths[i]).listFiles();
				for (int k = 0; k != files.length; k++) {
					File file = files[k];
					String name = files[k].getName();					
					if (name.contains(".") && file.isDirectory()) {
						return name.split("\\.")[0];
					}
				}
			}
		}
		return null;
	}
	
	public static String getChromeDriver(boolean update) {
		File driver = new File(Paths.get(Main.cd, "Users", Main.username, "AppData", "Local", "Temp", "chromedriver.exe").toString());
		if (!update && driver.exists()) {
			return driver.getAbsolutePath();
		}
		if (update && driver.exists()) {
			driver.delete();
		}
		System.out.println("Downloading driver...");
		try {
			String  cv = getChromeVersion();
			String cdv = "";
			File temp = new File(Paths.get(Main.cd, "Users", Main.username, "chrome.html").toString()); 
			Downloader.getHTML("https://chromedriver.storage.googleapis.com/", temp.getPath());
	        Scanner reader = new Scanner(temp);
	        while (reader.hasNextLine()) {
	        	cdv = cv + reader.nextLine().split("<Key>" + cv)[1].split("/")[0];
	        }
	        reader.close();
	        temp.delete();	        
	        String zip = Paths.get(Main.cd, "Users", Main.username, "chromedriver.zip").toString();
	        Downloader.download("https://chromedriver.storage.googleapis.com/" + cdv + "/chromedriver_win32.zip", zip);
	        Zipper.unzip(zip, Paths.get(Main.cd, "Users", Main.username, "AppData", "Local", "Temp").toString());
	        new File(zip).delete();
		   } catch (Exception e) {
			   return null;
		}
		System.out.println("Downloaded!");
		return driver.getAbsolutePath();
	}
}
