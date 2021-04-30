package org.yip.sandbox.jackson;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.yip.sandbox.fileio.IResources;
import org.yipuran.function.ThrowableFunction;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JsonNodeParseTest
 */
public class JsonNodeParseTest{
	private String jsontxt;
	private Map<String, Object> orgmap;
	private List<TFood> tfoods;
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
				Map.entry("group[0].name", "Red"),
				Map.entry("group[0].length", 192),
				Map.entry("group[1].name", "Yellow"),
				Map.entry("group[1].length", 26),
				Map.entry("group[2].name", "Green"),
				Map.entry("group[2].length", 34)
		);
		tfoods = List.of(new TFood("Red", 192), new TFood("Yellow", 26), new TFood("Green", 34));
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

	@Test
	public void testJsonStreamString2(){
		JsonNodeParse jparse = new JsonNodeParse()
		.addDeserilaize(Pattern.compile("date"), n->LocalDate.parse(n.asText(), DateTimeFormatter.ofPattern("yyyy/MM/dd")))
		.addDeserilaize(Pattern.compile("time"), n->LocalDateTime.parse(n.asText(), DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")))
		.addDeserilaize(Pattern.compile("group\\[\\d+\\]"), ThrowableFunction.of(new ObjectMapper().readerFor(TFood.class)::readValue));

		List<TFood> result = jparse.jsonStream(jsontxt).filter(e->e.getKey().startsWith("group[")).map(e->(TFood)(e.getValue())).collect(Collectors.toList());
		assertThat(result, is(tfoods));
	}

	@Test
	public void testJsonStreamString3(){
		JsonNodeParse jparse = new JsonNodeParse()
		.addDeserilaize(Pattern.compile("date"), n->LocalDate.parse(n.asText(), DateTimeFormatter.ofPattern("yyyy/MM/dd")))
		.addDeserilaize(Pattern.compile("time"), n->LocalDateTime.parse(n.asText(), DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")))
		.addDeserilaize(Pattern.compile("group\\[\\d+\\]"), JsonNodeParse.toFunction(TFood.class));

		List<TFood> result = jparse.jsonStream(jsontxt).filter(e->e.getKey().startsWith("group[")).map(e->(TFood)(e.getValue())).collect(Collectors.toList());
		assertThat(result, is(tfoods));
	}

}
