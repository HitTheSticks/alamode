package com.htssoft.alamode.files;

import java.io.File;
import java.io.IOException;

/**
 * Filename operations.
 * */
public class Filenames {
	
	/**
	 * Get a string represenation of the relative portion of a filename.
	 * 
	 * @param rootDir the root directory that you would like the path relative to.
	 * @param targetFile the file whose path you'd like.
	 * 
	 * @return the relative portion of the path, as a string.
	 * */
	public static String relativePath(File rootDir, File targetFile) throws IOException, IllegalArgumentException{
		String rootPath = rootDir.getCanonicalPath();
		String targetPath = targetFile.getCanonicalPath();
		
		if (targetPath.indexOf(rootPath) != 0){
			throw new IllegalArgumentException("The target file is not a strict child of the root path.");
		}
		
		return targetPath.substring(rootPath.length() + 1, targetPath.length());
	}
}
