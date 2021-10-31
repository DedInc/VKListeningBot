package com.github.dedinc.vklisteningbot.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Downloader {
	public static void getHTML(String url, String path) {
		try {
			if (!new File(path).exists()) {
				new File(path).createNewFile();
			}
			Files.write(Paths.get(new File(path).toURI()), Requests.get(url).getBytes(), StandardOpenOption.WRITE);
		   } catch (Exception e) {
	    }
	}

	public static void download(String url, String path) {
		while (new File(path).exists()) {
			try {
			    new File(path).delete();
			} catch (Exception e) {
				System.out.println("Please, close " + new File(path).getName() + " file!");
			}
		}
		try {
			InputStream source = new URL(url).openStream();
			Files.copy(source, Paths.get(path));
		   } catch (Exception e) {
	    }
	}
}
