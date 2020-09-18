package com.qualityobjects.springboot.specifications;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.Collection;
import java.util.Set;

public class QuerySpecification<T> implements Specification<T> {

	private static final long serialVersionUID = 6461819484732352113L;

	private transient SearchCriteria criteria;

	public QuerySpecification(SearchCriteria param) {
		this.criteria = param;
	}

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

		Path<?> fieldPath = null;

		if (criteria.getOperation() != FilterOperator.EXISTS && criteria.getOperation() != FilterOperator.NOT_EXISTS) {
			fieldPath = fieldPath(root, criteria.getKey());

			return locateOperation(fieldPath, builder);
		} else {
			return existOperation(builder, root, query);
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Predicate locateOperation(Path<?> fieldPath, CriteriaBuilder builder) {
		if (criteria.getOperation() == FilterOperator.LIKE) {
			return typeString(fieldPath, builder);
		} else if (criteria.getOperation() == FilterOperator.EQUAL) {
			return builder.equal(fieldPath, criteria.getValue());
		} else if (criteria.getOperation() == FilterOperator.NOT_EQUAL) {
			return builder.notEqual(fieldPath, criteria.getValue());
		} else if (criteria.getOperation() == FilterOperator.LOWER_THAN) {
			return builder.lessThan((Path<Comparable>) fieldPath, (Comparable) criteria.getValue());
		} else if (criteria.getOperation() == FilterOperator.GREATER_THAN) {
			return builder.greaterThan((Path<Comparable>) fieldPath, (Comparable) criteria.getValue());
		} else if (criteria.getOperation() == FilterOperator.LOWER_THAN_OR_EQUAL) {
			return builder.lessThanOrEqualTo((Path<Comparable>) fieldPath, (Comparable) criteria.getValue());
		} else if (criteria.getOperation() == FilterOperator.GREATER_THAN_OR_EQUAL) {
			return builder.greaterThanOrEqualTo((Path<Comparable>) fieldPath, (Comparable) criteria.getValue());
		} else if (criteria.getOperation() == FilterOperator.IS_NULL) {
			return builder.isNull(fieldPath);
		} else if (criteria.getOperation() == FilterOperator.IN) {
			return typeCollection(fieldPath, builder);
		} else if (criteria.getOperation() == FilterOperator.NOT_IN) {
			return typeNotCollection(fieldPath, builder);
		} else if (criteria.getOperation() == FilterOperator.NOT_NULL) {
			return builder.isNotNull(fieldPath);
		} else if (criteria.getOperation() == FilterOperator.IS_MEMBER) {
			return builder.isMember(criteria.getValue(), fieldPath.as(Set.class));
		} else if (criteria.getOperation() == FilterOperator.IS_NOT_MEMBER) {
			return builder.not(builder.isMember(criteria.getValue(), fieldPath.as(Set.class)));
		} else {
			return null;
		}
	}

	/**
	 * Método que devuelve Predicate con respecto a si es el tipo de operador no es
	 * collection
	 *
	 * @param fieldPath
	 * @param builder
	 * @return
	 */
	public Predicate typeNotCollection(Path<?> fieldPath, CriteriaBuilder builder) {
		if (criteria.getValue() instanceof Collection) {
			return builder.not(fieldPath.as(String.class).in(criteria.getValue()));
		} else {
			return builder.notEqual(fieldPath, criteria.getValue());
		}
	}

	/**
	 * Método que devuelve Predicate con respecto a si es el tipo de operador es
	 * collection
	 *
	 * @param fieldPath
	 * @param builder
	 * @return
	 */
	public Predicate typeCollection(Path<?> fieldPath, CriteriaBuilder builder) {
		if (criteria.getValue() instanceof Collection) {
			Object[] values = Collection.class.cast(criteria.getValue()).toArray();
			if (values.length > 0 && values[0] instanceof String) {
				return fieldPath.as(String.class).in(values);
			} else {
				return fieldPath.in(values);
			}
		} else {
			return builder.equal(fieldPath, criteria.getValue());
		}
	}

	/**
	 * Método que devuelve Predicate con respecto a si es el tipo de operador es
	 * String
	 *
	 * @param fieldPath
	 * @param builder
	 * @return
	 */
	public Predicate typeString(Path<?> fieldPath, CriteriaBuilder builder) {
		if (fieldPath.getJavaType() == String.class) {
			return builder.like(builder.lower(fieldPath.as(String.class)),
					"%" + criteria.getValue().toString().toLowerCase() + "%");
		} else {
			return builder.equal(fieldPath, criteria.getValue());
		}
	}

	/**
	 * Método que devuelve Predicate con respecto a si existe operador o no
	 *
	 * @param builder
	 * @param root
	 * @param query
	 * @return
	 */
	public Predicate existOperation(CriteriaBuilder builder, Root<T> root, CriteriaQuery<?> query) {
		if (criteria.getOperation() == FilterOperator.EXISTS) {
			Subquery<?> subquery = this.createExistsSubquery(root, query, builder,
					criteria.getSubQueryCriteria().getSubqueryClass());
			return builder.exists(subquery);
		} else if (criteria.getOperation() == FilterOperator.NOT_EXISTS) {
			Subquery<?> subquery = this.createExistsSubquery(root, query, builder,
					criteria.getSubQueryCriteria().getSubqueryClass());
			return builder.not(builder.exists(subquery));
		} else {
			return null;
		}
	}

	private <K> Subquery<Integer> createExistsSubquery(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder,
			Class<K> subqueryClass) {
		SearchCriteria.SubQueryCriteria subqueryCriteria = criteria.getSubQueryCriteria();

		Subquery<Integer> subQuery = query.subquery(Integer.class);
		Root<K> subRoot = subQuery.from(subqueryClass);
		subQuery.select(builder.literal(1));

		Predicate sqPredicate = null;
		if (subqueryCriteria.getCriteria() != null) {
			for (SearchCriteria sqCriteria : subqueryCriteria.getCriteria()) {
				Predicate predicate = new QuerySpecification<K>(sqCriteria).toPredicate(subRoot, query, builder);
				if (sqPredicate == null) {
					sqPredicate = predicate;
				} else {
					sqPredicate = builder.and(sqPredicate, predicate);
				}
			}
		} else {
			@SuppressWarnings("unchecked")
			Specification<K> subquerySpec = (Specification<K>) subqueryCriteria.getSpecification();
			if (subquerySpec != null) {
				sqPredicate = subquerySpec.toPredicate(subRoot, query, builder);
			}
		}
		if (subqueryCriteria.getParentRefCriteria() != null) {
			// Aquí el where ae.id_employee = [id empleado padre]
			Path<T> parentField = fieldPath(root, subqueryCriteria.getParentRefCriteria().getKey());
			Path<K> childField = fieldPath(subRoot, (String) subqueryCriteria.getParentRefCriteria().getValue());
			Predicate parentPredicate = builder.equal(parentField, childField);
			if (sqPredicate != null) {
				sqPredicate = builder.and(sqPredicate, parentPredicate);
			} else {
				sqPredicate = parentPredicate;
			}
		}

		// Aquí se unen todas las condiciones del where
		subQuery.where(sqPredicate);

		return subQuery;
	}

	private <Z> Path<Z> fieldPath(Path<Z> root, String fieldname) {
		String[] fields = fieldname.split("\\.");
		Path<Z> result = root;
		for (String field : fields) {
			result = result.get(field);
		}
		return result;
	}
}
