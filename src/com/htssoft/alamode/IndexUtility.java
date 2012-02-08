package com.htssoft.alamode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import com.htssoft.alamode.files.FileSignature;
import com.htssoft.alamode.files.RecursiveIndexer;

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
			signatures.addAll(processIndexTarget(f));
		}
		
		try {
			writeIndex(signatures);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	protected static Collection<FileSignature> processIndexTarget(File target){
		RecursiveIndexer indexer = new RecursiveIndexer(syncRoot, target);
		indexer.go();
		return indexer.getIndex();
	}
	
	protected static void writeIndex(List<FileSignature> signatures) throws IOException{
		for (FileSignature sig : signatures){
			System.out.println(sig);
		}
	}
}
