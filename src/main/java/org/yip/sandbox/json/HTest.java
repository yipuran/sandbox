package org.yip.sandbox.json;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * HTest.java
 */
public class HTest{

	/**
	 * @param args
	 */
	public static void main(String[] args){

		Map<String, Object> map = new HashMap<>();
		map.put("A", "anc");
		map.put("B", 12);
		map.put("C", Arrays.asList("x", "y", "z"));

		Gson gson = new GsonBuilder().serializeNulls().create();

		String jsonstr = gson.toJson(map);

		System.out.println(jsonstr);

		JsonClient client =
				JsonClientBuilder.of("http://localhost:8080/jacobtestweb/sample", jsonstr).build();

		client.execute((s, m)->{
			System.out.println("type = "+s);
			m.entrySet().stream().forEach(e->{
				System.out.println("key= "+e.getKey()+" ####");
				e.getValue().stream().forEach(t->{
					System.out.println(t);
				});
			});
		}, s->{
			System.out.println(s);
		});

	}

}
