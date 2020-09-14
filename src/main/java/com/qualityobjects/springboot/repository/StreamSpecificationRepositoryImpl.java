package com.qualityobjects.springboot.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.stream.Stream;

@Repository
@Transactional(readOnly = true)
public class StreamSpecificationRepositoryImpl<T> implements com.qualityobjects.springboot.repository.StreamSpecificationRepository<T> {

	@PersistenceContext
	private EntityManager em;
		
	@Override
	public Stream<T> streamAll(@Nullable Specification<T> spec, @Nullable Sort sort, Class<T> domainClass) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(domainClass);
        Root<T> root = query.from(domainClass);
        if (spec != null) {
        	query.where(spec.toPredicate(root, query, cb));
        }
        if (sort != null && !sort.equals(Sort.unsorted())) {
        	query.orderBy(sort.get().toArray(Order[]::new));
        }

        return em.createQuery(query).getResultStream();
	}


}
