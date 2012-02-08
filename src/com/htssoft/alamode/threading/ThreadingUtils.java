package com.htssoft.alamode.threading;

public class ThreadingUtils {
	
	/**
	 * Find out how many cores are available to the JVM.
	 * */
	public static int getCoreCount(){
		int retval = Runtime.getRuntime().availableProcessors();
		if (retval > 4){
			retval = 4;
		}
		return retval;
	}
}
