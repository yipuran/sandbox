package org.yip.sandbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.yipuran.function.ThrowableConsumer;
import org.yipuran.function.ThrowableFunction;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * IniFileParser.java
 *
 * [section] 必須
 * [section] の次に、key = value あるいは、key : value で記述
 *  シングル、ダブル クォート文字の特別な認識はしない。
 *  行で区切り
 *  先頭 '#' or ';' ～行末までコメント
 *
 * （機能）
 * 内部で持つMap への section＋Key 参照
 * JSON への変換
 *
 */
public final class IniFileParser{
	Map<String, List<Map.Entry<String, String>>> map = new HashMap<>();
	// section 無しの Map : key and value
	Map<String, String> nosectionMap = new HashMap<>();
	String key = null;

	private IniFileParser(Stream<String> stream) throws IllegalArgumentException{
		Pattern allblank = Pattern.compile("^ +$");
		Predicate<String> nosharp = e->!e.startsWith("#");
		Predicate<String> noempty = e->e.length() > 0;
		Predicate<String> noblank = e->!allblank.matcher(e).matches();
		AtomicInteger line = new AtomicInteger(0);
		AtomicReference<String> parseSection = new AtomicReference<String>(null);
		stream.peek(e->line.incrementAndGet())
		.filter(nosharp.and(e->!e.startsWith(";")).and(noempty).and(noblank))
		.map(ThrowableFunction.of(e->{
			if (e.substring(0, 1).equals("[")){
				if (!e.endsWith("]")) throw new IllegalArgumentException("Line:" + line.get() + " section Error : " + e);
				String section = e.substring(1, e.length() - 1);
				if (map.containsKey(section)) throw new IllegalArgumentException("Line:" + line.get() + " Duplicate secton Error : " + e);
				map.put(section, new ArrayList<>());
			}else{
				if (e.indexOf("=") < 0 && e.indexOf(":") < 0 ) throw new IllegalArgumentException("Line:" + line.get() + " unsolved Error : " + e);
			}
			return e;
		})).forEach(ThrowableConsumer.of(e->{
			if (e.substring(0, 1).equals("[")){
				// section
				String section = e.substring(1, e.length() - 1);
				parseSection.set(section);
			}else{
				String value = null;
				int x = e.indexOf("=");
				if (x > 0){
					key = e.substring(0, x).trim();
					value = e.substring(x+1);
				}else{
					x = e.indexOf(":");
					if (x > 0){
						key = e.substring(0, x).trim();
						value = e.substring(x+1);
					}
				}
				if (key.length()==0){
					throw new IllegalArgumentException("Line:" + line.get() + " Not key Error : " + e);
				}
				String section = parseSection.get();
				// value
				if (section==null){
					// No-section
					if (nosectionMap.containsKey(key)){
						throw new IllegalArgumentException("Line:" + line.get() + " Duplicate key Error : " + e);
					}
					nosectionMap.put(key, value);
				}else{
					// has section
					List<Map.Entry<String, String>> list = map.get(section);
					if (list.size() > 0){
						if (list.stream().anyMatch(t->t.getKey().equals(key))){
							throw new IllegalArgumentException("Line:" + line.get() + " Duplicate key Error : " + e);
						}
					}
					list.add(new _Entry(key, value));
				}
			}
		}));
	}
	class _Entry implements Map.Entry<String, String>{
		private String key;
		private String value;
		private _Entry(String key, String value){
			this.key = key;
			this.value = value;
		}
		@Override
		public String getKey(){
			return key;
		}
		@Override
		public String getValue(){
			return value;
		}
		@Override
		public String setValue(String value){
			String old = value;
			this.value = value;
			return old;
		}
	}

	public static IniFileParser read(InputStream in) throws IllegalArgumentException{
		BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
		return new IniFileParser(br.lines());
	}
	public static IniFileParser read(InputStream in, String charset) throws IllegalArgumentException{
		BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName(charset)));
		return new IniFileParser(br.lines());
	}

	public static IniFileParser read(String path) throws IOException, IllegalArgumentException{
		return new IniFileParser(Files.lines(FileSystems.getDefault().getPath("", path), Charset.forName("UTF-8")));
	}
	public static IniFileParser read(String path, String charset) throws IOException{
		return new IniFileParser(Files.lines(FileSystems.getDefault().getPath("", path), Charset.forName(charset)));
	}

	public static IniFileParser read(File file) throws IOException, IllegalArgumentException{
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")))){
			return new IniFileParser(br.lines());
		}
	}
	public static IniFileParser read(File file, String charset) throws IOException, IllegalArgumentException{
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName(charset)))){
			return new IniFileParser(br.lines());
		}
	}
	public static IniFileParser read(Reader reader) throws IllegalArgumentException{
		BufferedReader br = new BufferedReader(reader);
		return new IniFileParser(br.lines());
	}

	public String getString(String key){
		return Optional.ofNullable(nosectionMap.get(key)).map(e->e.trim()).orElse("");
	}
	public String getString(String section, String key){
		if (map.containsKey(section)){
			return map.get(section).stream().filter(e->e.getKey().equals(key)).findAny().map(e->e.getValue()).map(e->e.trim()).orElse("");
		}
		return "";
	}

	public Optional<Integer> getInteger(String key){
		return Optional.ofNullable(nosectionMap.get(key)).map(e->e.trim()).map(Integer::valueOf);
	}
	public Optional<Integer> getInteger(String section, String key){
		if (map.containsKey(section)){
			return map.get(section).stream().filter(e->e.getKey().equals(key)).findAny().map(e->e.getValue()).map(e->e.trim())
					.map(Integer::valueOf);
		}
		return Optional.empty();
	}

	public Optional<Double> getDouble(String key){
		return Optional.ofNullable(nosectionMap.get(key)).map(e->e.trim()).map(Double::valueOf);
	}
	public Optional<Double> getDouble(String section, String key){
		if (map.containsKey(section)){
			return map.get(section).stream().filter(e->e.getKey().equals(key)).findAny().map(e->e.getValue()).map(e->e.trim())
					.map(Double::valueOf);
		}
		return Optional.empty();
	}

	public Optional<BigDecimal> getBigDecimal(String key){
		return Optional.ofNullable(nosectionMap.get(key)).map(e->e.trim()).map(e->new BigDecimal(e));
	}
	public Optional<BigDecimal> getBigDecimal(String section, String key){
		if (map.containsKey(section)){
			return map.get(section).stream().filter(e->e.getKey().equals(key)).findAny().map(e->e.getValue()).map(e->e.trim())
					.map(e->new BigDecimal(e));
		}
		return Optional.empty();
	}

	public boolean getBoolean(String key){
		boolean b = Optional.ofNullable(nosectionMap.get(key)).map(e->e.trim()).map(e->{
			if (e.equals("1")){
				return true;
			}else if(e.equals("0")){
				return true;
			}
			String s = e.toLowerCase();
			if (s.equals("true")) return true;
			if (s.equals("false")) return false;
			throw new IllegalArgumentException("Not boolean value : " + e);
		}).orElse(false).booleanValue();
		return b;
	}
	public boolean getBoolean(String section, String key){
		if (map.containsKey(section)){
			boolean b = map.get(section).stream().filter(e->e.getKey().equals(key)).findAny().map(e->e.getValue()).map(e->e.trim())
			.map(e->{
				if (e.equals("1")){
					return true;
				}else if(e.equals("0")){
					return true;
				}
				String s = e.toLowerCase();
				if (s.equals("true")) return true;
				if (s.equals("false")) return false;
				throw new IllegalArgumentException("Not boolean value : " + e);
			}).orElse(false).booleanValue();
			return b;
		}
		return false;
	}

	public List<Entry<String, String>> getEntry(String section){
		return map.get(section);
	}
	public List<Entry<String, String>> getNosections(){
		return nosectionMap.entrySet().stream().collect(Collectors.toList());
	}

	public String toJson(){
		Gson gson = new GsonBuilder()	.serializeNulls().create();
		if (nosectionMap.size()==0)
			return gson.toJson(map , new TypeToken<Map<String, String>>(){}.getType());
		Map<String, Object> allmap = new HashMap<>();
		allmap.putAll(nosectionMap);
		allmap.putAll(map);
		return gson.toJson(allmap , new TypeToken<Map<String, String>>(){}.getType());
	}
}
