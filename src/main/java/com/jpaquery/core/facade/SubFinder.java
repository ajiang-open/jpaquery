package com.jpaquery.core.facade;

public interface SubFinder {
	public enum SubFinderType {
		exists, notExists, all, any, some;
	}
}
