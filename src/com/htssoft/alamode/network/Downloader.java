package com.htssoft.alamode.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Static class holding a download function.
 * */
public class Downloader {
	
	/**
	 * Downloads the file pointed to by the given URL into the path specified by the downloadDirectory
	 * and the relative filename.
	 * */
	public static void download(File downloadDirectory, URL url, String filename) throws IOException{
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		InputStream in = conn.getInputStream();
		
		File outfile = new File(downloadDirectory, filename);
		File directory = outfile.getParentFile();
		if (!directory.exists()){
			directory.mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(outfile);
		byte[] buffer = new byte[4096];
		int read = 0;
		while ((read = in.read(buffer)) >= 0){
			fos.write(buffer, 0, read);	
		}
		fos.close();
		in.close();
		conn.disconnect();
	}
}
