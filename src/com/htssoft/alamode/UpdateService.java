package com.htssoft.alamode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import com.htssoft.alamode.files.FileSignature;
import com.htssoft.alamode.files.SignatureCheckPipeline;
import com.htssoft.alamode.network.FakeDownloadPipeline;
import com.htssoft.alamode.network.UpdateSite;

public class UpdateService {
	protected UpdateSite updateSite;
	protected File downloadDir;
	protected File syncRoot;
	
	public UpdateService(String updatePropertiesFilename) throws IOException{
		Properties p = new Properties();
		FileInputStream fis = new FileInputStream(updatePropertiesFilename);
		p.load(fis);
		fis.close();
		
		updateSite = new UpdateSite(p.getProperty("update.site"));
		downloadDir = new File(".", p.getProperty("update.download.dir"));
		syncRoot = new File(System.getProperty("user.dir"));
	}
	
	protected int getLocalVersion() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("./version"));
		String vString = br.readLine();
		br.close();
		return Integer.parseInt(vString);
	}
	
	/**
	 * Is the remote version greater than the local version?
	 * */
	public boolean startupCheck() throws IOException{
		int remoteVersion = updateSite.getRemoteVersion();
		
		return remoteVersion > getLocalVersion();
	}
	
	/**
	 * Do an update into the local temporary location.
	 * @throws IOException 
	 * */
	public void doUpdate() throws IOException{
		if (downloadDir.exists()){
			deleteRecursive(downloadDir);
		}
		downloadDir.mkdir();
		
		SignatureCheckPipeline sigCheck = new SignatureCheckPipeline(syncRoot, 4);
		FakeDownloadPipeline download = new FakeDownloadPipeline(updateSite, downloadDir, 4, sigCheck.getOutputQueue());
		
		URL indexURL = updateSite.getIndexURL();
		HttpURLConnection conn = (HttpURLConnection) indexURL.openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		
		while ((line = br.readLine()) != null){
			if (line.length() < 1){
				continue;
			}
			try {
				sigCheck.submitWorkItem(new FileSignature(line));
			}
			catch (Exception ex){
				System.out.println("ERROR reading: " + line);
			}
		}
		sigCheck.awaitFinished();
		download.awaitFinished();
	}
	
	/**
     * By default File#delete fails for non-empty directories, it works like "rm". 
     * We need something a little more brutual - this does the equivalent of "rm -r"
     * @param path Root File Path
     * @return true iff the file and all sub files/directories have been removed
     * @throws FileNotFoundException
     */
    public static boolean deleteRecursive(File path) throws FileNotFoundException{
        if (!path.exists()){
        	return true;
        }
        boolean ret = true;
        if (path.isDirectory()){
            for (File f : path.listFiles()){
                ret = ret && deleteRecursive(f);
            }
        }
        return ret && path.delete();
    }

	
	/**
	 * Runs a default update sweep on the local directory.
	 * 
	 * Perhaps useful on its own.
	 * */
	public static void main(String[] args){
		try {
			UpdateService us = new UpdateService("alamode.prop");
			boolean doUpdate = us.startupCheck();
			if (doUpdate){
				System.out.println("Updating...");
				us.doUpdate();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
