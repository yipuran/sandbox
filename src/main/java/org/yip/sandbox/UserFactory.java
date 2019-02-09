package org.yip.sandbox;

import java.util.ArrayList;
import java.util.List;

/**
 * UserFactory
 */
public final class UserFactory{
	private UserFactory(){}
	public static List<User> creeate3(){
		List<User> list = new ArrayList<>();
		list.add(new User(1, "A", 904));
		list.add(new User(2, "B", 1286));
		list.add(new User(3, "C", 1286));
		list.add(new User(4, "D", 700));
		list.add(new User(5, "E", 800));
		list.add(new User(6, "F", 700));
		list.add(new User(7, "G", 500));

		return list;
	}


}
