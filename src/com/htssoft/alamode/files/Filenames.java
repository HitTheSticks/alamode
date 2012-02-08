package com.htssoft.alamode.files;

import java.io.File;
import java.io.IOException;

public class Filenames {
	public static String relativePath(File rootDir, File targetFile) throws IOException, IllegalArgumentException{
		String rootPath = rootDir.getCanonicalPath();
		String targetPath = targetFile.getCanonicalPath();
		
		if (targetPath.indexOf(rootPath) != 0){
			throw new IllegalArgumentException("The target file is not a strict child of the root path.");
		}
		
		return targetPath.substring(rootPath.length(), targetPath.length());
	}
}
