package com.htssoft.alamode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.htssoft.alamode.files.FileSignature;
import com.htssoft.alamode.files.RecursiveIndexer;
import com.htssoft.alamode.threading.FinishableQueue;

/**
 * This class implements a tool to index files for an alamode update site.
 * */
public class IndexUtility {
	protected static File syncRoot;
	
	public static void main(String[] args){
		if (args.length < 1){
			System.out.println("USAGE: IndexUtility syncTarget1 [syncTarget2...]");
			return;
		}
		
		syncRoot = new File(".");
		
		Vector<FileSignature> signatures = new Vector<FileSignature>();
		
		for (int i = 0; i < args.length; i++){
			File f = new File(syncRoot, args[i]);
			processIndexTarget(f).copyContents(signatures);
		}
		
		try {
			writeIndex(signatures);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Index one sync target.
	 * */
	protected static FinishableQueue<FileSignature> processIndexTarget(File target){
		RecursiveIndexer indexer = new RecursiveIndexer(syncRoot, target);
		indexer.go();
		return indexer.getIndex();
	}
	
	/**
	 * Sort the index according to md5 sum, and write it out to "alamode.index" in the 
	 * current directory.
	 * */
	protected static void writeIndex(List<FileSignature> signatures) throws IOException{
		FileWriter writer = new FileWriter("alamode.index");
		Collections.sort(signatures);
		for (FileSignature sig : signatures){
			writer.append(sig.toString());
			writer.append("\n");
		}
		writer.close();
	}
}
