package com.htssoft.alamode.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import sun.misc.Cleaner;
import sun.nio.ch.DirectBuffer;

/**
 * A reusable file hashing object.
 * */
public class FileHasher {
	protected MessageDigest hashImpl;
	
	/**
	 * Initialize the md5 implementation.
	 * */
	public void init() throws NoSuchAlgorithmException {
		hashImpl = MessageDigest.getInstance("md5");
	}
	
	/**
	 * Hashes a file into a FileSignature.
	 * */
	public FileSignature hashFile(File syncRoot, File queryFile) throws IOException {
		if (!queryFile.exists() || queryFile.length() == 0){
			return new FileSignature(Filenames.relativePath(syncRoot, queryFile), 0, "");
		}
		
		FileInputStream fis = new FileInputStream(queryFile);
		FileChannel chan = fis.getChannel();
		
		
		MappedByteBuffer byteBuffer = chan.map(MapMode.READ_ONLY, 0, queryFile.length());
		
		Cleaner cleaner = null;
		try {
			cleaner = ((DirectBuffer) byteBuffer).cleaner();
		}
		catch (Exception ex){
			System.err.println("Cannot get cleaner.");
		}
		
		hashImpl.reset();
		hashImpl.update(byteBuffer);
		byte[] digest = hashImpl.digest();
			
		if (cleaner != null){
			cleaner.clean();
		}
		
		chan.close();
		fis.close();
		
		FileSignature retval =  new FileSignature(Filenames.relativePath(syncRoot, queryFile), 
												  queryFile.length(), 
												  MD5Converter.convert(digest));
		
		
		
		return retval;
	}
	
	/**
	 * A trivial main function that hashes a given file in the current directory.
	 * */
	public static void main(String[] args){
		FileHasher fh = new FileHasher();
		
		try {
		fh.init();
		
		FileSignature fs = fh.hashFile(new File("."), new File(args[0]));
		System.out.println(fs.getMd5());
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
	}
}
