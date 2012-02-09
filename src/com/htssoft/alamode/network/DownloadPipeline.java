package com.htssoft.alamode.network;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.htssoft.alamode.threading.FinishableQueue;
import com.htssoft.alamode.threading.ThreadedPipeline;

/**
 * A pipeline that downloads items directly over existing files.
 * */
public class DownloadPipeline extends ThreadedPipeline<String, Object>{
	protected File downloadDirectory;
	protected UpdateSite site;
	
	public DownloadPipeline(UpdateSite site, File downloadDirectory, int nThreads, FinishableQueue<String> input) {
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
	
	
	protected void download(URL url, String filename) throws IOException {
		Downloader.download(downloadDirectory, url, filename);
	}
}
