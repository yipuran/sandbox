package org.yip.sandbox.pch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
/**
 * 重複順列.
 * <PRE>
 * 要素の重複有りの順列（Permutation）
 *
 * 使用例
 * RepeatablePermutation<Integer> r = RepeatablePermutation.of(ilist);
 * r.compute(2).stream().map(e->e.stream().map(i->i.toString()).collect(Collectors.joining(""))).forEach(System.out::println);
 *
 * RepeatablePermutation<String> rs = RepeatablePermutation.of(Arrays.asList("A", "B", "C"));
 * rs.compute(3).stream().map(e->e.stream().collect(Collectors.joining(""))).forEach(System.out::println);
 * </PRE>
 *
 * @since Ver 4.14
 */
public class RepeatablePermutation<T>{
	private int g;
	private T[] c;
	private int[] p;
	private List<List<T>> result;
	private List<T> one;
	private Predicate<List<T>> predicate;
	private Consumer<List<T>> consumer;

	@SuppressWarnings("unchecked")
	private RepeatablePermutation(List<T> list){
		c = (T[])list.toArray();
		g = c.length;
		p = new int[g];
	}
	/**
	 * List→インスタンス生成.
	 * @param list List&lt;T&gt;生成前のリスト
	 * @return RepeatablePermutation
	 */
	public static <T> RepeatablePermutation<T> of(List<T> list){
		return new RepeatablePermutation<>(list);
	}
	/**
	 * 配列→インスタンス生成.
	 * @param larray T[] 生成前の配列
	 * @return RepeatablePermutation
	 */
	public static <T> RepeatablePermutation<T> of(T[] ary){
		return new RepeatablePermutation<>(Arrays.asList(ary));
	}
	/**
	 * 重複あり順列結果リスト抽出.
	 * @param len 順列 nPr の r
	 * @return  List&lt;List&lt;T&gt;&gt;
	 */
	public List<List<T>> compute(int len){
		if (len > c.length) throw new IllegalArgumentException("list size over");
		result = new ArrayList<>();
		for(int i = 0; i < len; i++){
			calc(0, i, len);
		}
		return result;
	}
	private void calc(int i, int j, int len){
		p[i] = j;
		if (i == len - 1) {
			List<T> list = new ArrayList<>();
			for(int n = 0; n < len; n++) {
				list.add(c[p[n]]);
			}
			result.add(list);
		}
		if (i < g - 1){
			i++;
			for(int k = 0; k < len; k++){
				calc(i, k, len);
			}
		}
	}
	/**
	 * 重複あり組み合わせ順列結果List のイテレータを返す
	 * @param len 順列 nPr の r
	 * @return Iterator<List<T>>
	 */
	public Iterator<List<T>> iterator(int len){
		return this.compute(len).iterator();
	}

	public void compute(int len, Predicate<List<T>> predicate, Consumer<List<T>> consumer){
		if (len > c.length) throw new IllegalArgumentException("list size over");
		this.predicate = predicate;
		this.consumer = consumer;
		for(int i = 0; i < len; i++){
			calcx(0, i, len);
		}
	}

	private void calcx(int i, int j, int len){
		p[i] = j;
		if (i == len - 1) {
			List<T> list = new ArrayList<>();
			for(int n = 0; n < len; n++) {
				list.add(c[p[n]]);
			}
			if (predicate.test(list)) {
				consumer.accept(list);
			}
		}
		if (i < g - 1){
			i++;
			for(int k = 0; k < len; k++){
				calcx(i, k, len);
			}
		}
	}

	public List<T> computeFirst(int len, Predicate<List<T>> predicate){
		if (len > c.length) throw new IllegalArgumentException("list size over");
		this.predicate = predicate;
		one = null;
		for(int i = 0; i < len; i++){
			if (calcxx(0, i, len)) break;
		}
		return one;
	}

	private boolean calcxx(int i, int j, int len){
		p[i] = j;
		if (i == len - 1) {
			List<T> list = new ArrayList<>();
			for(int n = 0; n < len; n++) {
				list.add(c[p[n]]);
			}
			if (predicate.test(list)) {
				one = list;
				return true;
			}
		}
		if (i < g - 1){
			i++;
			for(int k = 0; k < len; k++){
				if (calcxx(i, k, len)) return true;
			}
		}
		return false;
	}

	public List<List<T>> compute(int len, Predicate<List<T>> predicate){
		if (len > c.length) throw new IllegalArgumentException("list size over");
		result = new ArrayList<>();
		this.predicate = predicate;
		for(int i = 0; i < len; i++){
			calcy(0, i, len);
		}
		return result;
	}

	private void calcy(int i, int j, int len){
		p[i] = j;
		if (i == len - 1) {
			List<T> list = new ArrayList<>();
			for(int n = 0; n < len; n++) {
				list.add(c[p[n]]);
			}
			if (predicate.test(list)) result.add(list);
		}
		if (i < g - 1){
			i++;
			for(int k = 0; k < len; k++){
				calcy(i, k, len);
			}
		}
	}
}
