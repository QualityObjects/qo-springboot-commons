package com.qualityobjects.springboot.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import java.util.stream.Stream;

public interface StreamSpecificationRepository<T> {

   /**
	 * Returns all entities matching the given {@link Specification}.
	 * 
	 * @param spec can be {@literal null}.
	 * @return never {@literal null}.
	 */
	public Stream<T> streamAll(@Nullable Specification<T> spec, @Nullable Sort sort, Class<T> domainClass);

	/**
	 * Returns all entities matching the given {@link Specification}.
	 *
	 * @param spec can be {@literal null}.
	 * @return never {@literal null}.
	 */
	public default Stream<T> streamAll(@Nullable Specification<T> spec, Class<T> domainClass) {
		return this.streamAll(spec, null, domainClass);
	}

}
