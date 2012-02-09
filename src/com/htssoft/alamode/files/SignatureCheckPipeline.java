package com.htssoft.alamode.files;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.htssoft.alamode.threading.ThreadedPipeline;

/**
 * A pipeline that checks FileSignatures against local files, and
 * outputs (relative) filenames that differ.
 * */
public class SignatureCheckPipeline extends ThreadedPipeline<FileSignature, String> {
	protected ThreadLocal<FileHasher> hasher = new ThreadLocal<FileHasher>(){
		public FileHasher initialValue(){
			FileHasher retval = new FileHasher();
			try {
				retval.init();
			} catch (NoSuchAlgorithmException ex) {
				ex.printStackTrace();
			}
			return retval;
		}
	};
	protected File syncRoot;
	
	
	public SignatureCheckPipeline(File syncRoot, int nThreads) {
		super(nThreads);
		this.syncRoot = syncRoot;
	}

	@Override
	protected String processItem(FileSignature item) {
		File target = new File(syncRoot, item.getName());
		try {
			FileSignature localSig = hasher.get().hashFile(syncRoot, target);
			if (!item.equals(localSig)){
				System.out.println(String.format("%s -> %s", item.toString(), localSig.toString()));
				output.add(item.getName());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

}
