package com.htssoft.alamode.network;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

import com.htssoft.alamode.threading.FinishableQueue;

public class FakeDownloadPipeline extends DownloadPipeline {

	public FakeDownloadPipeline(UpdateSite site, File downloadDirectory, int nThreads, FinishableQueue<String> input) {
		super(site, downloadDirectory, nThreads, input);
	}
	
	protected void download(URL url, String filename) throws IOException{
		System.out.println(url.toString());
		File outfile = new File(downloadDirectory, filename);
		System.out.println("Would Download: " + outfile.getCanonicalPath());
	}
}