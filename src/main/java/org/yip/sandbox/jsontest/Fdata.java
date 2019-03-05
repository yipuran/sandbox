package org.yip.sandbox.jsontest;

import java.io.Serializable;
import java.util.List;

/**
 * Udata
 */
public class Fdata implements Serializable{
	public List<String> list;
	public List<Integer> vlist;
	public List<Foo> flist;
	public int val;
	public Fdata(){}
	public Foo foo;

}
