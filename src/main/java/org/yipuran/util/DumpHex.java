package org.yipuran.util;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * DumpHex.java.
 */
public final class DumpHex{
	private String str;
	private static String spacer = "                                  ";
	private DumpHex(String str){
		this.str = str;
	}
	public static DumpHex of(String str){
		return new DumpHex(str);
	}
	public Stream<String> stream(){
		byte[] bs = str.getBytes();
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(Hexbyterator.of(bs, 16), 0), false)
		.map(b->{
			StringBuilder sb = new StringBuilder();
			for(int x=0;x < b.length;x++){
				if (x > 0 && x % 4 == 0) sb.append(" ");
				sb.append(String.format("%02x", b[x]));
			}
			String hexstring = sb.toString();
			if (35 > hexstring.length()){
				hexstring += spacer.substring(0, 35 - hexstring.length());
			}
			for(int n=0;n < b.length;n++){
				if (b[n] < 0){
					b[n] = 0x3f;
				}else if(b[n] < 0x20){
					b[n] = 0x2e;
				}
			}
			String ss = new String(b);
			return hexstring + "  [" + ss + "]";
		});
	}
	public List<String> list(){
		return stream().collect(Collectors.toList());
	}
	public String hexstring(){
		return hexstring("");
	}
	public String hexstring(String splitstring){
		ByteArrayInputStream inst = new ByteArrayInputStream(str.getBytes());
		return Stream.generate(inst::read).limit(inst.available())
		.map(e->String.format("%02x", e)).collect(Collectors.joining(splitstring));
	}

}
