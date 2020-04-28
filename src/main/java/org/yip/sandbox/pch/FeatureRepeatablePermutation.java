package org.yip.sandbox.pch;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
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
public class FeatureRepeatablePermutation<T>{
	private int r, u;
	private int[] c;
	private int[] p;
	private List<T> list;
	private List<List<T>> result;
	private List<T[]> resultAry;
AtomicLong counter;
	private FeatureRepeatablePermutation(List<T> list){
		this.list = list;
		c = new int[list.size()];
		p = new int[list.size()];
		for(int i=0; i < c.length; i++) c[i] = i;
		counter = new AtomicLong(0);
	}
	/**
	 * List→インスタンス生成.
	 * @param list List&lt;T&gt;生成前のリスト
	 * @return RepeatablePermutation
	 */
	public static <T> FeatureRepeatablePermutation<T> of(List<T> list){
		return new FeatureRepeatablePermutation<>(list);
	}
	/**
	 * 配列→インスタンス生成.
	 * @param larray T[] 生成前の配列
	 * @return RepeatablePermutation
	 */
	public static <T> FeatureRepeatablePermutation<T> of(T[] ary){
		return new FeatureRepeatablePermutation<>(Arrays.asList(ary));
	}
	/**
	 * 重複あり順列結果リスト抽出.
	 * @param len 順列 nPr の r
	 * @return  List&lt;List&lt;T&gt;&gt;
	 */
	public List<List<T>> compute(int len){
		if (len > list.size()) throw new IllegalArgumentException("list size over");
		result = new ArrayList<>();
		r = len;
		u = c[c.length-1] - len;
		for(int i=0; i <= c.length; i++)
			calc(0, i);
		return result;
	}
	private void calc(int i, int j){
		p[i] = j;
		if (i == r - 1){
			List<T> t = new ArrayList<>();
			for(int k=0; k < r; k++){
				if (p[k] < c.length)
					t.add(list.get(c[p[k]]));
			}
			if (t.size()==r)
				result.add(t);
		}
		if (i < r - 1){
			int k=0;
			for(; k <= r;k++)
				calc(i+1, k);
			if (u > 0){
				for(int n=0; n < u; n++)
					calc(i+1, c[k + n] );
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

	private Predicate<List<T>> predicateList;
	private Consumer<List<T>> consumerList;

	public void anyMatch(int len, Predicate<List<T>> predicate, Consumer<List<T>> consumer){
		if (len > list.size()) throw new IllegalArgumentException("list size over");
		this.predicateList = predicate;
		this.consumerList = consumer;
		r = len;
		u = c[c.length-1] - len;
		for(int i=0; i <= c.length; i++)
			calcX(0, i);
	}
	private void calcX(int i, int j){
		p[i] = j;
		if (i == r - 1){
			List<T> t = new ArrayList<>();
			for(int k=0; k < r; k++){
				if (p[k] < c.length)
					t.add(list.get(c[p[k]]));
			}
			if (t.size()==r){
				if (predicateList.test(t)) consumerList.accept(t);
			}
		}
		if (i < r - 1){
			int k=0;
			for(; k <= r;k++)
				calcX(i+1, k);
			if (u > 0){
				for(int n=0; n < u; n++)
					calcX(i+1, c[k + n] );
			}
		}
	}
	public List<T> firstMatch(int len, Predicate<List<T>> predicate){
		if (len > list.size()) throw new IllegalArgumentException("list size over");
		this.predicateList = predicate;
		r = len;
		u = c[c.length-1] - len;
		for(int i=0; i <= c.length; i++) {
			List<T> a = calcY(0, i);
			if (a != null) return a;
		}
		return null;
	}
	private List<T> calcY(int i, int j){
		p[i] = j;
		if (i == r - 1){
			List<T> t = new ArrayList<>();
			for(int k=0; k < r; k++){
				if (p[k] < c.length)
					t.add(list.get(c[p[k]]));
			}
			if (t.size()==r){
				if (predicateList.test(t)) return t;
			}
		}
		if (i < r - 1){
			int k=0;
			for(; k <= r;k++){
				List<T> a = calcY(i+1, k);
				if (a != null) return a;
			}
			if (u > 0){
				for(int n=0; n < u; n++){
					List<T> a = calcY(i+1, c[k + n]);
					if (a != null) return a;
				}
			}
		}
		return null;
	}

	public List<T[]> computeArray(int len){
		if (len > list.size()) throw new IllegalArgumentException("list size over");
		r = len;
		u = c[c.length-1] - len;
		for(int i=0; i <= c.length; i++)
			calcAry(0, i);
		return resultAry;
	}
	@SuppressWarnings("unchecked")
	private void calcAry(int i, int j){
		p[i] = j;
		if (i == r - 1){
			T[] tt = (T[])Array.newInstance(list.get(0).getClass(), r);
			int cn = 0;
			for(int k=0; k < r; k++){
				if (p[k] < c.length) {
					tt[k] = list.get(c[p[k]]);
					cn++;
				}
			}
			if (cn==r){
				resultAry.add(tt);
			}
		}
		if (i < r - 1){
			int k=0;
			for(; k <= r;k++)
				calcAry(i+1, k);
			if (u > 0){
				for(int n=0; n < u; n++)
					calcAry(i+1, c[k + n] );
			}
		}
	}

	private Predicate<T[]> predicateAry;
	private Consumer<T[]> consumerAry;
	public void anyMatchArray(int len, Predicate<T[]> predicate, Consumer<T[]> consumer){
		if (len > list.size()) throw new IllegalArgumentException("list size over");
		this.predicateAry = predicate;
		this.consumerAry = consumer;
		r = len;
		u = c[c.length-1] - len;
		for(int i=0; i <= c.length; i++)
			calcAryX(0, i);
	}
	@SuppressWarnings("unchecked")
	private void calcAryX(int i, int j){
		p[i] = j;
		if (i == r - 1){
			T[] tt = (T[])Array.newInstance(list.get(0).getClass(), r);
			int cn = 0;
			for(int k=0; k < r; k++){
				if (p[k] < c.length) {
					tt[k] = list.get(c[p[k]]);
					cn++;
				}
			}
			if (cn==r){
				if (predicateAry.test(tt)) consumerAry.accept(tt);
			}
		}
		if (i < r - 1){
			int k=0;
			for(; k <= r;k++)
				calcAryX(i+1, k);
			if (u > 0){
				for(int n=0; n < u; n++)
					calcAryX(i+1, c[k + n] );
			}
		}
	}

	public T[] firstMatchArray(int len, Predicate<T[]> predicate){
		if (len > list.size()) throw new IllegalArgumentException("list size over");
		this.predicateAry = predicate;
		r = len;
		u = c[c.length-1] - len;
		for(int i=0; i <= c.length; i++) {
			T[] a = calcAryY(0, i);
			if (a != null) return a;
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	private T[] calcAryY(int i, int j){
		p[i] = j;
		if (i == r - 1){
			T[] tt = (T[])Array.newInstance(list.get(0).getClass(), r);
			int cn = 0;
			for(int k=0; k < r; k++){
				if (p[k] < c.length) {
					tt[k] = list.get(c[p[k]]);
					cn++;
				}
			}
			if (cn==r){
				counter.incrementAndGet();
				if (predicateAry.test(tt)) return tt;
			}
		}
		if (i < r - 1){
			int k=0;
			for(; k <= r;k++) {
				T[] a = calcAryY(i+1, k);
				if (a != null) return a;
			}
			if (u > 0){
				for(int n=0; n < u; n++) {
					T[] a = calcAryY(i+1, c[k + n] );
					if (a != null) return a;
				}
			}
		}
		return null;
	}

	public long count() {
		return counter.get();
	}

}
