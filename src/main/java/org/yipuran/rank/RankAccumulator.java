package org.yipuran.rank;

import java.util.List;
import java.util.ListIterator;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * ソート済のリストに順位を振る accumulator.
 * <PRE>
 * 順位を振ることが可能なソートが済んでいるリストに対して、または、ソート済のStreamに対して、
 * ・純粋なシーケンシャルな順位を採番
 * もしくは、
 * ・同率順位の個数分をスキップした採番
 * のどちらかを指定できます。
 * Stream の終端操作、collect メソッドの第２引数 accumulator を提供します。
 * （使用例）
 *    User オブジェクトが以下の構成を持つ。
 *       public class User{
 *          public String name;
 *          public int point;  // ソート対象の値
 *          public int rank;   // 順位
 *       }
 *    ソートされた User リストの順位を示す rank属性に目的の順位を割り振ります。
 *
 *    シーケンシャル順位付け（値降順）の場合、、
 *       List&lt;User&gt; list;
 *       // list に格納
 *       list.stream().sorted((a, b)-&gt;Integer.valueOf(b.point).compareTo(Integer.valueOf(a.point)))
 *       .collect(()-&gt;new ArrayList&lt;User&gt;(), RankAccumulator.seek(t-&gt;t.point, (t, u)-&gt;t.rank=u), (r, u)-&gt;{});
 *
 *    スキップ順位付け（値降順）の場合、、
 *       List&lt;User&gt; list;
 *       // list に格納
 *       list.stream().sorted((a, b)-&gt;Integer.valueOf(b.point).compareTo(Integer.valueOf(a.point)))
 *       .collect(()->new ArrayList&lt;User&gt;(), RankAccumulator.skip(t-&gt;t.point, (t, u)-&gt;t.rank=u), (r, u)-&gt;{});
 * </PRE>
 */
public final class RankAccumulator{
	/** private constructor. */
	private RankAccumulator(){}

	/**
	 * シーケンシャル順位付け.
	 * @param getValue 比較値取得 Function&lt;T, Integer&gt;
	 * @param setRank 順位設定 BiConsumer&lt;T, Integer&gt;
	 * @return BiConsumer&lt;R, T&gt;
	 */
	public static <R extends List<T>, T> BiConsumer<R, T> seek(Function<T, Integer> getValue, BiConsumer<T, Integer> setRank){
		return new BiConsumer<R, T>(){
			int count = 1;
			@Override
			public void accept(R r, T t){
				if (r.isEmpty()){
					setRank.accept(t, 1);
				}else{
					T pre = r.get(r.size()-1);
					if (getValue.apply(pre).intValue() != getValue.apply(t).intValue()){
						count++;
					}
					setRank.accept(t, count);
				}
				r.add(t);
			}
		};
	}
	/**
	 * スキップ順位付け.
	 * @param getValue 比較値取得 Function&lt;T, Integer&gt;
	 * @param setRank 順位設定 BiConsumer&lt;T, Integer&gt;
	 * @return BiConsumer&lt;R, T&gt;
	 */
	public static <R extends List<T>, T> BiConsumer<R, T> skip(Function<T, Integer> getValue, BiConsumer<T, Integer> setRank){
		return new BiConsumer<R, T>(){
			int count = 1;
			@Override
			public void accept(R r, T t){
				int rsize = r.size();
				if (rsize==0){
					setRank.accept(t, 1);
				}else{
					T last = r.get(r.size()-1);
					if (getValue.apply(last).intValue() == getValue.apply(t).intValue()){
						setRank.accept(t, count);
					}else{
						int inc = 0;
						for(ListIterator<T> it=r.listIterator(rsize); it.hasPrevious();){
							T pre = it.previous();
							if (getValue.apply(last).intValue() != getValue.apply(pre).intValue()) break;
							inc++;
						}
						if (inc > 0){
							count += inc;
						}else {
							count++;
						}
						setRank.accept(t, count);
					}
				}
				r.add(t);
			}
		};
	}
}
