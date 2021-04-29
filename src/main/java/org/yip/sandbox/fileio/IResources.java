package org.yip.sandbox.fileio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * IResources
 */
public interface IResources{

	public static String readResource(String path) {
		try(InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(path)){
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			int i;
			while((i = in.read()) > 0) {
				bo.write(i);
			}
			bo.flush();
			bo.close();
			return bo.toString();
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}

	public static InputStream readResourceStream(String path) {
		return ClassLoader.getSystemClassLoader().getResourceAsStream(path);
	}

	public static Reader readResourceReader(String path) {
		return new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(path), StandardCharsets.UTF_8);
	}
	public static Reader readResourceReader(String path, Charset cs) {
		return new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(path), cs);
	}
}
