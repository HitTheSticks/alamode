package com.htssoft.alamode.files;

import java.io.File;

import com.htssoft.alamode.threading.FinishableQueue;
import com.htssoft.alamode.threading.ThreadingUtils;

/**
 * Indexes files recursively, building an index of files.
 * 
 * Only *files* are listed in the index, not directories. Directories
 * are implicitly handled merely by having files in them.
 * */
public class RecursiveIndexer {
	protected File syncRoot;
	protected File targetRoot;
	protected SignaturePipeline signatures;
	
	/**
	 * Create an indexer for the given recursion target, relative to the syncRoot.
	 * */
	public RecursiveIndexer(File syncRoot, File recursionTarget){
		this.syncRoot = syncRoot;
		this.targetRoot = recursionTarget;
		if (!syncRoot.isDirectory()){
			throw new IllegalArgumentException("Sync root must be a directory.");
		}
		signatures = new SignaturePipeline(syncRoot, ThreadingUtils.getCoreCount());
	}
	
	/**
	 * Do the index.
	 * */
	public void go(){
		if (targetRoot.isDirectory()){
			indexDirectory(targetRoot);
		}
		else {
			indexFile(targetRoot);
		}
		signatures.awaitFinished();
	}
	
	/**
	 * Get the index.
	 * */
	public FinishableQueue<FileSignature> getIndex(){
		return signatures.getOutputQueue();
	}
	
	/**
	 * For files, add them to the signatures pipeline.
	 * 
	 * For directories, recurse.
	 * */
	protected void indexDirectory(File dir){
		for (File f : dir.listFiles()){
			if (f.isDirectory()){
				indexDirectory(f);
				continue;
			}
			else {
				indexFile(f);
			}
		}
	}
	
	/**
	 * Add a file to the signature pipeline.
	 * */
	protected void indexFile(File f){
		if (f.getAbsolutePath().contains(" ")){
			System.err.println("Filenames with spaces are not supported. This index is corrupt now.");
		}
		signatures.submitWorkItem(f);
	}
	
	/**
	 * Bogus, useless unit test.
	 * */
	public static void main(String[] args){
		RecursiveIndexer indexer = new RecursiveIndexer(new File("."), new File("./assets"));
		indexer.go();
		for (FileSignature fs : indexer.getIndex()){
			System.out.println(fs);
		}
	}
}
