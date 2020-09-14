package com.qualityobjects.springboot.specifications;

import com.qualityobjects.commons.exception.QORuntimeException;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class SearchCriteria {
		
	private String key;
    private FilterOperator operation;
    private Object value;
    private SubQueryCriteria subQueryCriteria;
    
    public SearchCriteria(String key, FilterOperator operation, Object value) {
    	this.key = key;
    	this.operation = operation;
    	this.value = value;
    	if (this.operation == FilterOperator.EXISTS) {
    		throw new QORuntimeException("El operador EXISTS requiere información de subquery");
    	}
    	this.subQueryCriteria = null;
    }

	public static SearchCriteria of(String key, FilterOperator operation, Object value) {
		return new SearchCriteria(key, operation, value);
    }

    /**
     * Crea un SearchCriteria de tipo (operador) "EXISTS" con información para la subquery pero sin referencia entre la query parent y la subquery
     * 
     * @param operator Debe ser FilterOperator.EXISTS o FilterOperator.NOT_EXISTS;
     * @param subqueryClass Clase Entity sobre la qe se creará la subquery 
     * @param sqCriteria Condiciones de la propia subquery
     */
	public static SearchCriteria of(FilterOperator operator, Class<?> subqueryClass, List<SearchCriteria> sqCriteria) {
		return of(operator, subqueryClass, sqCriteria, null);
	}

    /**
     * Crea un SearchCriteria de tipo (operador) "EXISTS" con información para la subquery 
     * 
     * @param operator Debe ser FilterOperator.EXISTS o FilterOperator.NOT_EXISTS;
     * @param subqueryClass Clase Entity sobre la qe se creará la subquery 
     * @param sqCriteria Condiciones de la propia subquery
     * @param parentRef Datos para vincular los elementos de la subquery con el elemento correspondiente de la query padre
     */
    public static SearchCriteria of(FilterOperator operator, Class<?> subqueryClass, List<SearchCriteria> sqCriteria, SearchCriteria parentRef) {
    	if (operator != FilterOperator.EXISTS && operator != FilterOperator.NOT_EXISTS) {
    		throw new QORuntimeException("El operador debe ser EXISTS o NOT_EXISTS");
		}
    	if (parentRef != null && parentRef.operation != FilterOperator.EQUAL) {
    		throw new QORuntimeException("El operador EQUALS es obligaotoro en este tipo de SearchCriteria");
		}
		SearchCriteria sc = new SearchCriteria(null, null, null);
    	sc.setOperation(operator);
    	sc.subQueryCriteria = new SubQueryCriteria();
    	sc.subQueryCriteria.criteria = sqCriteria;
    	sc.subQueryCriteria.parentRefCriteria = parentRef;
    	sc.subQueryCriteria.subqueryClass = subqueryClass;
		return sc;
    }

    /**
     * Crea un SearchCriteria para un filtro por referencia al padre en uan subquery
	 * 
	 * @param parentField Nombre del atributo de la entidad "padre"
	 * @param childField Nombre del atributo de la entidad "hija" (la entidad dentro de la subquery)
	 * @return
	 */
	public static SearchCriteria parentRef(String parentField, String childField) {
		return new SearchCriteria(parentField, FilterOperator.EQUAL, childField);
	}

    /**
     * Crea un SearchCriteria de tipo (operador) "EXISTS" con información para la subquery 
     * 
     * @param subqueryClass Clase Entity sobre la qe se creará la subquery 
     * @param spec Filtro ya montado al "estilo spring" para la query
     * @param parentRef Datos para vincular los elementos de la subquery con el elemento correspondiente de la query padre
     */
	public static SearchCriteria of(FilterOperator operator, Class<?> subqueryClass, Specification<?> spec, SearchCriteria parentRef) {
		SearchCriteria sc = SearchCriteria.of(operator, subqueryClass, (List<SearchCriteria>)null, parentRef);
		sc.subQueryCriteria.specification = spec;
		return sc;
	}
    

    public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public FilterOperator getOperation() {
		return operation;
	}

	public void setOperation(FilterOperator operation) {
		this.operation = operation;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public SubQueryCriteria getSubQueryCriteria() {
		return subQueryCriteria;
	}

	public void setSubQueryCriteria(SubQueryCriteria subQueryCriteria) {
		this.subQueryCriteria = subQueryCriteria;
	}




	public static class SubQueryCriteria {
    	private Class<?> subqueryClass;
        private List<SearchCriteria> criteria;
        private Specification<?> specification;
        private SearchCriteria parentRefCriteria;
        
		public Class<?> getSubqueryClass() {
			return subqueryClass;
		}
		public void setSubqueryClass(Class<?> subqueryClass) {
			this.subqueryClass = subqueryClass;
		}
		public List<SearchCriteria> getCriteria() {
			return criteria;
		}
		public void setCriteria(List<SearchCriteria> criteria) {
			this.criteria = criteria;
		}
		public Specification<?> getSpecification() {
			return specification;
		}
		public void setSpecification(Specification<?> specification) {
			this.specification = specification;
		}
		public SearchCriteria getParentRefCriteria() {
			return parentRefCriteria;
		}
		public void setParentRefCriteria(SearchCriteria parentRefCriteria) {
			this.parentRefCriteria = parentRefCriteria;
		}
        
        
    }
}