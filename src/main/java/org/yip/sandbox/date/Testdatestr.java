package org.yip.sandbox.date;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.yipuran.util.Fieldgetter;

/**
 * Testdatestr
 */
public class Testdatestr{
	public static void main(String[] args){

		String s = "20200429";
		int len = 4;

		DataItem di = new DataItem();

		LocalDate d = LocalDate.parse(s, DateTimeFormatter.ofPattern("yyyyMMdd"));

		Long sum =	IntStream.range(0, len).boxed().map(i->d.plusDays(i).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
		.map(e->Fieldgetter.of(t->"price"+e.substring(6)).apply(di))
		.map(e->Integer.valueOf(e.toString()))
		//.forEach(System.out::println);
		.collect(Collectors.summarizingInt(i->i)).getSum();

		System.out.println(sum);

		int a = IntStream.range(0, len).boxed().map(i->d.plusDays(i).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
		.map(e->Fieldgetter.of(t->"price"+e.substring(6)).apply(di))
		.mapToInt(e->Integer.valueOf(e.toString()))
		.sum();

		System.out.println(a);
	}

}
