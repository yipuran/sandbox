package org.yip.sandbox.pch;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.yipuran.util.pch.Homogeneous;

/**
 * Testhomogen
 */
public class Testhomogen{
	public static void main(String[] args){

		List<List<String>> hlist = Homogeneous.of(Arrays.asList("A","B","C")).compute(4);

		hlist.stream()
		.filter(e->e.stream().anyMatch(s->s.equals("B")))
		// TODO  .map( list → sort )
		.map(e->e.stream().collect(Collectors.joining(""))).forEach(System.out::println);
	}

}
