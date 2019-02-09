package org.yipuran.util;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Hexbyterator.java.
 */
public class Hexbyterator implements Iterator<byte[]>, Serializable{
	private byte[] b;
	private boolean next;
	private long seek = 0;
	private int blength = 16;

	private Hexbyterator(byte[] b, int len){
		if (len < 1) new IllegalArgumentException("pack length must be greater than 0");
		this.b = b;
		blength = len;
		next = b != null && b.length > 0 ? true : false;
	}
	public static Hexbyterator of(byte[] bytes, int len){
		return new Hexbyterator(bytes, len);
	}
	@Override
	public boolean hasNext(){
		return next;
	}
	@Override
	public byte[] next(){
		if (next){
			long n = seek + blength;
			if ( n < b.length ){
				byte[] r = new byte[blength];
				for(int i=0;i < blength;i++){
					r[i] = b[Long.valueOf(seek).intValue()];
					seek++;
				}
				next = seek < b.length;
				return r;
			}
			byte[] r = new byte[ Long.valueOf(blength - (n - b.length)).intValue() ];
			for(int i=0;i < r.length;i++){
				r[i] = b[Long.valueOf(seek).intValue()];
				seek++;
			}
			next = false;
			return r;
		}
		return null;
	}

}
