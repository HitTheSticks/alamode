package com.htssoft.alamode.network;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import com.htssoft.alamode.threading.FinishableQueue;

/**
 * A testing mock that simply prints out what it *would* download.
 * */
public class FakeDownloadPipeline extends DownloadPipeline {

	public FakeDownloadPipeline(UpdateSite site, File downloadDirectory, int nThreads, FinishableQueue<String> input) {
		super(site, downloadDirectory, nThreads, input, null);
	}
	
	protected void download(URL url, String filename) throws IOException{
		File outfile = new File(downloadDirectory, filename);
		System.out.println(String.format("Would Download %s -> %s", url.toString(), outfile.getCanonicalPath()));
	}
}
