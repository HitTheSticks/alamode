package com.htssoft.alamode.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Describes an update location.
 * */
public class UpdateSite {
	protected String urlFragment;
	
	public UpdateSite(String updateSiteURLFragment){
		urlFragment = updateSiteURLFragment;
	}
	
	public URL getFileURL(String filename) throws MalformedURLException{
		return new URL(String.format("%s/%s", urlFragment, filename));
	}
	
	public String getRemoteVersion() throws IOException {
		URL versionURL = getFileURL("version");
		
		HttpURLConnection conn = (HttpURLConnection) versionURL.openConnection();
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK){
			throw new IOException("Bad HTTP response.");
		}
		
		InputStream in = conn.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line = br.readLine();
		br.close();
		
		return line;
	}
	
	/**
	 * Get the URL representing the index.
	 * */
	public URL getIndexURL() throws IOException {
		return getFileURL("alamode.index");
	}
	
	public static void main(String[] args){
		UpdateSite us = new UpdateSite("http://localhost/updates");
		try {
			URL u = us.getFileURL("assets/grid/stripe_fog.png");
			System.out.println(u.toString());
			System.out.println(us.getRemoteVersion());
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
