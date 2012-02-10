package com.htssoft.alamode.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

/**
 * Describes an update location.
 * */
public class UpdateSite {
	protected String urlFragment;
	protected URL siteURL;

	/**
	 * Create an UpdateSite from the url fragment specifying it.
	 * @throws MalformedURLException 
	 * */
	public UpdateSite(String updateSiteURLFragment) throws MalformedURLException{
		if (!updateSiteURLFragment.endsWith("/")){
			urlFragment = updateSiteURLFragment.concat("/");
		}
		else {
			urlFragment = updateSiteURLFragment;
		}
		
		
		siteURL = new URL(urlFragment);
	}
	
	protected String sanitizePath(String path){
		if (path.matches("[\\w/]+")){
			return path;
		}
		String[] splits = path.split("/");
		
		StringBuilder sb = new StringBuilder();
		
		
		for (int i = 0 ; i < splits.length - 1 ; i++){
			try {
				sb.append(URLEncoder.encode(splits[i], "UTF-8")).append('/');
			} catch (UnsupportedEncodingException ex) {
				ex.printStackTrace();
			}
		}
		
		if (splits.length > 0){
			try {
				sb.append(URLEncoder.encode(splits[splits.length - 1], "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				ex.printStackTrace();
			}
		}
		
		
		return sb.toString();
	}
	
	/**
	 * Get the URL of a distribution file.
	 * */
	public URL getFileURL(String filename) throws MalformedURLException{
		return new URL(siteURL, sanitizePath(filename));
	}
	
	/**
	 * Get the remote version.
	 * */
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
	
	/**
	 * Useless unit test.
	 * */
	public static void main(String[] args){
		try {
			UpdateSite us = new UpdateSite("http://localhost/updates/");
			URL u = us.getFileURL("assets/grid/stripe_ fog.png");
			System.out.println(u.toString());
			System.out.println(us.getRemoteVersion());
			
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
}
