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

/**
 * This implements the client-side synchronization facility.
 * */
public class UpdateService {
	public static boolean FAKE_RUN = false;
	
	protected UpdateSite updateSite;
	protected File downloadDir;
	protected File syncRoot;
	protected ProgressPrinter progressCallback;
	
	/**
	 * Create a new UpdateService.
	 * 
	 * @param rootDirectory the root directory to synchronize.
	 * @param updatePropertiesFilename a path (absolute, relative, whatever) to the alamode.props file defining
	 * 	the update site parameters.
	 * */
	public UpdateService(File rootDirectory, String updatePropertiesFilename) throws IOException{
		Properties p = new Properties();
		FileInputStream fis = new FileInputStream(updatePropertiesFilename);
		p.load(fis);
		fis.close();
		
		updateSite = new UpdateSite(p.getProperty("update.site"));
		
		syncRoot = rootDirectory;
		downloadDir = syncRoot;
	}
	
	/**
	 * Set the progress printer.
	 * */
	public void setProgressCallback(ProgressPrinter callback){
		progressCallback = callback;
	}
	
	/**
	 * Get the local version. This is an md5 hash of the
	 * alamode.index in the local sync directory. Or, an 
	 * empty string if the file doesn't exist.
	 * */
	public String getLocalVersion() throws IOException{
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
	 * Is the local version different from the update site's version?
	 * */
	public boolean remoteVersionMismatch() throws IOException{
		String remoteVersion = updateSite.getRemoteVersion();
		String localVersion = getLocalVersion();
		
		return !remoteVersion.equals(localVersion);
	}
	
	/**
	 * Downloads the remote alamode.index.
	 * */
	public void getIndex() throws IOException{
		Downloader.download(syncRoot, updateSite.getIndexURL(), "alamode.index");
	}
	
	/**
	 * Scans all files listed by alamode.index, and downloads all files whose
	 * md5 differs from the indexed hash. 
	 * */
	public void doUpdate() throws IOException{
		if (!downloadDir.exists()){
			return;
		}
		
		SignatureCheckPipeline sigCheck = new SignatureCheckPipeline(syncRoot, 4);
		
		DownloadPipeline download;
		if (!FAKE_RUN){
			download = new DownloadPipeline(updateSite, downloadDir, 4, sigCheck.getOutputQueue(), progressCallback);
		}
		else {
			download = new FakeDownloadPipeline(updateSite, downloadDir, 4, sigCheck.getOutputQueue());
		}
		
		BufferedReader br = new BufferedReader(new FileReader(new File(syncRoot, "alamode.index")));
		String line;
		
		while ((line = br.readLine()) != null){
			if (line.length() < 1){
				continue;
			}
			try {
				FileSignature sig = new FileSignature(line);
				sigCheck.submitWorkItem(sig);
			}
			catch (Exception ex){
				System.out.println("ERROR reading: " + line);
			}
		}
		br.close();
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
	 * This runs a complete update cycle on the current working directory.
	 * */
	public static void main(String[] args){
		for (String s : args){
			if (s.equals("-f")){
				UpdateService.FAKE_RUN = true;
			}
		}
		try {
			UpdateService us = new UpdateService(new File(System.getProperty("user.dir")), "alamode.prop");
			boolean doUpdate = us.remoteVersionMismatch();
			if (doUpdate){
				us.getIndex();
			}
			us.doUpdate();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
