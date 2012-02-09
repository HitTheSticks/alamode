package com.htssoft.alamode.files;

/**
 * A file is known by its name, its length,
 * and its md5 hash.
 * */
public class FileSignature implements Comparable<FileSignature>{
	protected final String name;
	protected final long length;
	protected final String md5;
	
	/**
	 * Creates a FileSignature from a comma-separated list of name, length, md5.
	 * */
	public FileSignature(String line){
		String[] splits = line.split(",");
		name = splits[0].trim();
		length = Long.parseLong(splits[1].trim());
		if (length != 0){
			md5 = splits[2].trim();
		}
		else {
			md5 = "";
		}
	}
	
	/**
	 * Creates a file signature from name, length, and md5.
	 * */
	public FileSignature(String name, long length, String md5){
		this.name = name.replace('\\', '/');
		this.length = length;
		this.md5 = md5;
	}
	
	/**
	 * FileSignatures are equal only if name, length, and md5 are all equal.
	 * */
	public boolean equals(FileSignature o){
		return this.name.equals(o.name) &&
			   this.length == o.length &&
			   this.md5.equals(o.md5);
	}
	
	public int hashCode(){
		return md5.hashCode();
	}
	
	/**
	 * The name of the file, relative to the synchronization root.
	 * */
	public String getName() {
		return name;
	}

	/**
	 * The length of the file in bytes.
	 * */
	public long getLength() {
		return length;
	}

	/**
	 * The md5 string signature of the file, in hex.
	 * */
	public String getMd5() {
		return md5;
	}
	
	/**
	 * A comma-separated list describing the signature.
	 * */
	public String toString(){
		return String.format("%s, %d, %s", name, length, md5);
	}

	@Override
	public int compareTo(FileSignature o) {
		return this.md5.compareTo(o.md5);
	}

}
