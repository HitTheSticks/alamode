package com.htssoft.alamode.files;

import java.io.File;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import com.htssoft.alamode.threading.ThreadingUtils;

public class RecursiveIndexer {
	protected File syncRoot;
	protected File targetRoot;
	protected SignaturePipeline signatures;
	
	public RecursiveIndexer(File syncRoot, File recursionTarget){
		this.syncRoot = syncRoot;
		this.targetRoot = recursionTarget;
		if (!syncRoot.isDirectory()){
			throw new IllegalArgumentException("Sync root must be a directory.");
		}
		signatures = new SignaturePipeline(syncRoot, ThreadingUtils.getCoreCount());
	}
	
	public void go(){
		if (targetRoot.isDirectory()){
			indexDirectory(targetRoot);
		}
		else {
			indexFile(targetRoot);
		}
		signatures.awaitFinished();
	}
	
	public LinkedBlockingQueue<FileSignature> getIndex(){
		return signatures.getOutputQueue();
	}
	
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
	
	protected void indexFile(File f){
		signatures.submitWorkItem(f);
	}
	
	public static void main(String[] args){
		RecursiveIndexer indexer = new RecursiveIndexer(new File("."), new File("./assets"));
		indexer.go();
		for (FileSignature fs : indexer.getIndex()){
			System.out.println(fs);
		}
	}
}
