package org.yip.sandbox.pch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Permutation
 */
public class Permutation<T>{
	private List<T> list;
	private int     number, list_size, searched, next_index;
	private int[][] perm_list;

	public static <T> Permutation<T> of(List<T> list){
		return new Permutation<>(list);
	}
	/**
	 * @param list List<T>
	 */
	public Permutation(List<T> list){
		this.list = list;
	}

	public List<List<T>> compute(int r){
		if (r > list.size()) throw new IllegalArgumentException("list size over");
		this.number     = r;
		this.list_size  = this.fact(r);
		this.searched   = 0;
		this.next_index = 0;
		this.perm_list  = new int[this.list_size][r];
		this.create(0, new int[r], new boolean[r]);
		List<List<T>> rtn = new ArrayList<List<T>>();
		while(isNext()){
			rtn.add(Arrays.stream(nextPerm()).boxed().map(e->list.get(e)).collect(Collectors.toList()));
		}
		return rtn;
	}
	public Iterator<List<T>> iterator(int len){
		return compute(len).iterator();
	}
	private int[] nextPerm(){
		this.next_index++;
		return perm_list[this.next_index-1];
	}
	private boolean isNext(){
		if(this.next_index < this.list_size) {
			return true;
		}else{
			this.next_index = 0;
			return false;
		}
	}
	private int fact(int n){
		return n == 0 ? 1 : n * fact(n-1);
	}
	private void create(int _num, int[] _list, boolean[] _flag) {
		if(_num == this.number) {
			copyArray(_list, perm_list[this.searched]);
			this.searched++;
		}
		for(int i=0;i < _list.length;i++) {
			if(_flag[i]) continue;
			_list[_num] = i;
			_flag[i] = true;
			this.create(_num+1, _list, _flag);
			_flag[i] = false;
		}
	}
	private void copyArray(int[] _from, int[] _to) {
		for(int i=0; i<_from.length; i++) _to[i] = _from[i];
	}
}