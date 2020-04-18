package org.yip.sandbox.rangemodel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.yipuran.util.Fieldsetter;
import org.yipuran.util.GenericBuilder;

/**
 * RangeCoverTest
 */
public class RangeCoverTest{
	public static void main(String[] args){
		List<Eunit> list = createList(2020, 4, 5,7,12, 30);

		int stay = 11;
		int last = list.size() - stay + 1;

		AtomicInteger x = new AtomicInteger(0);

		Stream<Eunit> beforstream = list.subList(0, last).stream()
		.map(e->{
			int i = x.getAndIncrement();
			List<Eunit> sublist = list.subList(i, i + stay);
			if (list.subList(i, i + stay).stream().anyMatch(t->!t.status.equals("○"))) {
				e.status = "×";
			}
			System.out.println(e + " " + sublist );
			return e;
		});

		Stream<Eunit> afterstream = list.subList(last, list.size()).stream()
		.map(e->{
			e.status = "×";
			return e;
		});
		List<Eunit> newlist = Stream.concat(beforstream, afterstream).collect(Collectors.toList());
		System.out.println("--------------  last = " + 20);

		newlist.stream().forEach(System.out::println);
	}
	private static List<Eunit> createList(int year, int month, long...ngdays){
		List<Eunit> rtn = new ArrayList<Eunit>();
		IntStream ngstream = Arrays.stream(ngdays).mapToInt(e->(int)e);
		List<Integer> nglist = ngstream.boxed().collect(Collectors.toList());

		LocalDate start = LocalDate.of(year, month, 1);
		List<Eunit> list = Stream.iterate(start, t->t.plusDays(1)).limit(start.lengthOfMonth())
		.map(e->GenericBuilder.of(Eunit::new).with(Fieldsetter.of((t, u)->"day"), e).build()).collect(Collectors.toList());
		rtn = list.stream().map(e->{
					if (nglist.stream().anyMatch(i->i==e.day.getDayOfMonth())) {
						e.status = "×";
					}else{
						e.status = "○";
					}
					return e;
				})
				.collect(Collectors.toList());
		return rtn;
	}
}
