package com.htssoft.alamode.files;

/**
 * A file is known by its name, its length,
 * and its md5 hash.
 * */
public class FileSignature {
	protected final String name;
	protected final long length;
	protected final String md5;
	
	public FileSignature(String name, long length, String md5){
		this.name = name;
		this.length = length;
		this.md5 = md5;
	}
	
	public boolean equals(FileSignature o){
		return this.name.equals(o.name) &&
			   this.length == o.length &&
			   this.md5.equals(o.md5);
	}
	
	public int hashCode(){
		return md5.hashCode();
	}
	
	public String getName() {
		return name;
	}

	public long getLength() {
		return length;
	}

	public String getMd5() {
		return md5;
	}
	
	public String toString(){
		return String.format("%s, %d, %s", name, length, md5);
	}

}
