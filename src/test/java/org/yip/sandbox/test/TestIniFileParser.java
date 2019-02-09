package org.yip.sandbox.test;

import java.io.FileInputStream;
import java.io.IOException;

import org.yip.sandbox.IniFileParser;


/**
 * TestIniFileParser.java
 */
public class TestIniFileParser{

	/**
	 * @param args
	 */
	public static void main(String[] args){
		try(FileInputStream in = new FileInputStream(ClassLoader.getSystemClassLoader().getResource("sample.ini").getPath())){
			IniFileParser iniParser = IniFileParser.read(in);

			System.out.println("keys = [" + iniParser.getString("keys") + "]" );
			System.out.println( "customer.name = [" + iniParser.getString("customer", "city") + "]" );
			System.out.println( "customer.name = [" + iniParser.getString("customer", "name") + "]" );
			System.out.println( "customer.master = [" + iniParser.getBoolean("customer", "master") + "]" );
			System.out.println( "server.port = [" + iniParser.getInteger("server", "port").orElseThrow() + "]" );
			System.out.println( "server.name = [" + iniParser.getString("server", "password") + "]" );
			String json = iniParser.toJson();
			System.out.println(json);

		}catch(IOException e){
			e.printStackTrace();
		}

	}

}
