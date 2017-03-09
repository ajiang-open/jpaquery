package com.jpaquery.core.vo;

import java.util.ArrayList;
import java.util.List;

import com.jpaquery.core.impl.JoinPathImpl;

/**
 * From信息
 * 
 * @author lujijiang
 * 
 */
public class FromInfo {
	/**
	 * 实体信息
	 */
	EntityInfo<?> entityInfo;

	/**
	 * Join信息
	 */
	List<JoinPathImpl<?>> joinPaths = new ArrayList<>();

	public EntityInfo<?> getEntityInfo() {
		return entityInfo;
	}

	public List<JoinPathImpl<?>> getJoinPaths() {
		return joinPaths;
	}

	public FromInfo(EntityInfo<?> entityInfo) {
		super();
		this.entityInfo = entityInfo;
	}

}
