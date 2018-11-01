package com.jpaquery.core;

import com.jpaquery.core.facade.JpaQuery;

public interface QueryHandler<T> {
    T handle(JpaQuery query);
}
