package com.htssoft.alamode.files;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.htssoft.alamode.threading.ThreadedPipeline;

/**
 * A pipeline that computes signatures.
 * */
public class SignaturePipeline extends ThreadedPipeline<File, FileSignature> {
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
	
	public SignaturePipeline(File syncRoot, int nThreads) {
		super(nThreads);
		this.syncRoot = syncRoot;
	}

	
	@Override
	protected FileSignature processItem(File item) {
		try {
			return hasher.get().hashFile(syncRoot, item);
		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
