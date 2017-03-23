package com.jpaquery.support.primeng;

import org.springframework.data.domain.PageRequest;

public class PrimePageRequest extends PageRequest {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8107286350650255670L;

	public PrimePageRequest() {
		super(0, 25);
	}

}
