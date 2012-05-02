package com.htssoft.alamode.network;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.htssoft.alamode.ProgressPrinter;
import com.htssoft.alamode.threading.FinishableQueue;
import com.htssoft.alamode.threading.ThreadedPipeline;

/**
 * A pipeline that downloads items directly over existing files.
 * */
public class DownloadPipeline extends ThreadedPipeline<String, Object>{
	protected File downloadDirectory;
	protected UpdateSite site;
	protected ProgressPrinter progressCallback;
	
	public DownloadPipeline(UpdateSite site, File downloadDirectory, int nThreads, FinishableQueue<String> input, ProgressPrinter progressCallback) {
		super(nThreads, input);
		this.downloadDirectory = downloadDirectory;
		this.site = site;
		this.progressCallback = progressCallback;
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
		if (progressCallback != null){
			progressCallback.printProgress(filename);
		}
		Downloader.download(downloadDirectory, url, filename);
	}
}
