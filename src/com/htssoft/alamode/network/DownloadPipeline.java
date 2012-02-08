package com.htssoft.alamode.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

import com.htssoft.alamode.threading.ThreadedPipeline;

public class DownloadPipeline extends ThreadedPipeline<String, Object>{
	protected File downloadDirectory;
	protected UpdateSite site;
	
	public DownloadPipeline(UpdateSite site, File downloadDirectory, int nThreads, LinkedBlockingQueue<String> input) {
		super(nThreads, input);
		this.downloadDirectory = downloadDirectory;
		this.site = site;
	}

	@Override
	protected Object processItem(String item) {
		try {
			URL u = site.getFileURL(item);
			download(u, item);
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}		
		
		return null;
	}
	
	
	protected void download(URL url, String filename) throws IOException{
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
	}
}
