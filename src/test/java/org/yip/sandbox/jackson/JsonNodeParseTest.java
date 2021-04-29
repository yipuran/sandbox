package org.yip.sandbox.jackson;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.yip.sandbox.fileio.IResources;

/**
 * JsonNodeParseTest
 */
public class JsonNodeParseTest{
	private String jsontxt;
	private Map<String, Object> orgmap;
	@Before
	public void before() {
		jsontxt = IResources.readResource("sample.json");
		orgmap = Map.ofEntries(
				Map.entry("id",1002),
				Map.entry("flg",true),
				Map.entry("name", "氏名"),
				Map.entry("date",	"2021/04/30"),
				Map.entry("time",	"2021/04/30 14:16:08"),
				Map.entry("ary[0]", 1),
				Map.entry("ary[1]", 12),
				Map.entry("ary[2]", 37),
				Map.entry("group[0].item", "Red"),
				Map.entry("group[0].len", 192),
				Map.entry("group[1].item", "Yellow"),
				Map.entry("group[1].len", 26),
				Map.entry("group[2].item", "Green"),
				Map.entry("group[2].len", 34)
		);
	}

	@Test
	public void testReadJsonStringBiConsumerOfStringObject(){
		Map<String, Object> map = new HashMap<>();;
		JsonNodeParse jparse = new JsonNodeParse();
		jparse.readJson(jsontxt, (p, o)->map.put(p, o));
		assertThat(orgmap, is(map));
	}
	@Test
	public void testJsonStreamString(){
		JsonNodeParse jparse = new JsonNodeParse()
		.addDeserilaize(Pattern.compile("date"), n->LocalDate.parse(n.asText(), DateTimeFormatter.ofPattern("yyyy/MM/dd")))
		.addDeserilaize(Pattern.compile("time"), n->LocalDateTime.parse(n.asText(), DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));

		Object o1 = jparse.jsonStream(jsontxt).filter(e->e.getKey().equals("date")).map(e->e.getValue()).findAny().get();
		assertThat(o1, is(LocalDate.of(2021, 4, 30)));

		Object o2 = jparse.jsonStream(jsontxt).filter(e->e.getKey().equals("time")).map(e->e.getValue()).findAny().get();
		assertThat(o2, is(LocalDateTime.of(2021, 4, 30, 14, 16, 8)));
	}

}
