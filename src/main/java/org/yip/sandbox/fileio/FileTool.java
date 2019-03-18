package org.yip.sandbox.fileio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Optional;

/**
 * FileTool.java
 */
public final class FileTool {
	private FileTool() {
	}

	public static byte[] readBinary(String path) throws IOException {
		try{
			File file = Optional.ofNullable(
				ClassLoader.getSystemClassLoader()
				.getResource(Class.forName(Thread.currentThread()
				.getStackTrace()[2].getClassName())
				.getPackageName().replaceAll("\\.", "/") + "/" + path))
			.map(u->{
				try{
					return new File(u.toURI());
				}catch(URISyntaxException e){
					return null;
				}
			}).orElse(new File(path));
			try(InputStream in = new FileInputStream(file)){
				byte[] data = new byte[in.available()];
				in.read(data);
				in.close();
				return data;
			}
		}catch(ClassNotFoundException ex){
			throw new IOException(ex.getMessage(), ex);
		}
	}

	public static String readText(String path) throws IOException {
		try{
			File file = Optional.ofNullable(
				ClassLoader.getSystemClassLoader()
				.getResource(Class.forName(Thread.currentThread()
				.getStackTrace()[2].getClassName())
				.getPackageName().replaceAll("\\.", "/") + "/" + path))
			.map(u->{
					try{
						return new File(u.toURI());
					}catch(URISyntaxException e){
						return null;
					}
			}).orElse(new File(path));
			try(InputStream in = new FileInputStream(file);
				ByteArrayOutputStream out = new ByteArrayOutputStream()){
				in.transferTo(out);
				return out.toString();
			}
		}catch(ClassNotFoundException ex){
			throw new IOException(ex.getMessage(), ex);
		}
	}
	public static Reader getReader(String path) throws IOException {
		try{
			File file = Optional.ofNullable(
				ClassLoader.getSystemClassLoader()
				.getResource(Class.forName(Thread.currentThread()
				.getStackTrace()[2].getClassName())
				.getPackageName().replaceAll("\\.", "/") + "/" + path))
			.map(u->{
					try{
						return new File(u.toURI());
					}catch(URISyntaxException e){
						return null;
					}
			}).orElse(new File(path));
			return new FileReader(file);
		}catch(ClassNotFoundException ex){
			throw new IOException(ex.getMessage(), ex);
		}
	}
	public static Reader getReader(String path, Charset charset) throws IOException {
		try{
			File file = Optional.ofNullable(
				ClassLoader.getSystemClassLoader()
				.getResource(Class.forName(Thread.currentThread()
				.getStackTrace()[2].getClassName())
				.getPackageName().replaceAll("\\.", "/") + "/" + path))
			.map(u->{
					try{
						return new File(u.toURI());
					}catch(URISyntaxException e){
						return null;
					}
			}).orElse(new File(path));
			return new FileReader(file, charset);
		}catch(ClassNotFoundException ex){
			throw new IOException(ex.getMessage(), ex);
		}
	}

	public static void write(String text, String path) throws IOException{
		try(OutputStream out = new FileOutputStream(path)){
			out.write(text.getBytes());
			out.flush();
		}
	}

	public static void write(byte[] data, String path) throws IOException{
		try(OutputStream out = new FileOutputStream(path)){
			out.write(data, 0, data.length);
			out.flush();
		}
	}
}
