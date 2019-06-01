package org.yip.sandbox.typeahead;

import java.io.Serializable;

/**
 * twitter/Typeahead.js element.
 */
public class Typeahead implements Serializable{
	public String id;
	public String value;

	public Typeahead(){}

	public void setValue(String value){
		this.value = value;
	}
	public void setId(String id){
		this.id = id;
	}
}
