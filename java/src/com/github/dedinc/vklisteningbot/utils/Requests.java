package com.github.dedinc.vklisteningbot.utils;

import okhttp3.OkHttpClient;
import okhttp3.Response;

public class Requests {
	
	public static String get(String url) {
		try {
			OkHttpClient client = new OkHttpClient();
			okhttp3.Request req = new okhttp3.Request.Builder()
					.url(url)
					.build();
			Response res = client.newCall(req).execute();
			return res.body().string();
		  } catch (Exception e) {
		}
		return null;
	 }
}
