package com.htssoft.alamode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import com.htssoft.alamode.files.FileHasher;
import com.htssoft.alamode.files.FileSignature;
import com.htssoft.alamode.files.SignatureCheckPipeline;
import com.htssoft.alamode.network.DownloadPipeline;
import com.htssoft.alamode.network.Downloader;
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
		
		syncRoot = new File(System.getProperty("user.dir"));
		downloadDir = syncRoot;
	}
	
	protected String getLocalVersion() throws IOException{
		FileHasher hasher = new FileHasher();
		try {
			hasher.init();
		}
		catch (NoSuchAlgorithmException ex){
			ex.printStackTrace();
			return "";
		}
		
		return hasher.hashFile(syncRoot, new File(syncRoot, "alamode.index")).getMd5();
	}
	
	/**
	 * Is the remote version greater than the local version?
	 * */
	public boolean startupCheck() throws IOException{
		String remoteVersion = updateSite.getRemoteVersion();
		String localVersion = getLocalVersion();
		
		return !remoteVersion.equals(localVersion);
	}
	
	public void getIndex() throws IOException{
		Downloader.download(syncRoot, updateSite.getIndexURL(), "alamode.index");
	}
	
	/**
	 * Do an update into the local temporary location.
	 * @throws IOException 
	 * */
	public void doUpdate() throws IOException{
		if (!downloadDir.exists()){
			return;
		}
		
		SignatureCheckPipeline sigCheck = new SignatureCheckPipeline(syncRoot, 4);
		DownloadPipeline download = new DownloadPipeline(updateSite, downloadDir, 4, sigCheck.getOutputQueue());
		
		BufferedReader br = new BufferedReader(new FileReader("alamode.index"));
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
				us.getIndex();
			}
			us.doUpdate();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
