package org.yip.sandbox.jackson;

import lombok.Data;

/**
 * TFood
 */
@Data
public class TFood{
	private String name;
	private int length;
	public TFood(){}
	public TFood(String name, int length){
		this.name = name;
		this.length = length;
	}
}
