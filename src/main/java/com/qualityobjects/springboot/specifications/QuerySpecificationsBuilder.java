package com.qualityobjects.springboot.specifications;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class QuerySpecificationsBuilder<T> {

	private List<Specification<T>> specs;
    
    public QuerySpecificationsBuilder() {
    	specs = new ArrayList<>();
    }
 
    public QuerySpecificationsBuilder<T> with(SearchCriteria criteria) {
        specs.add(new QuerySpecification<T>(criteria));
        return this;
    }
    
    public QuerySpecificationsBuilder<T> with(String key, FilterOperator operation, Object value) {
        specs.add(new QuerySpecification<T>(new SearchCriteria(key, operation, value)));
        return this;
    }
    
    public QuerySpecificationsBuilder<T> with(LogicalOperator op, SearchCriteria... criteria) {
    	Specification<T> spec = partialBuild(op, criteria);
    	specs.add(spec);
        return this;
    }
    
    public QuerySpecificationsBuilder<T> with(LogicalOperator op, List<SearchCriteria> criterias) {
    	Specification<T> spec = partialBuild(op, criterias);
    	specs.add(spec);
        return this;
    }
 
    public QuerySpecificationsBuilder<T> with(Specification<T> spec) {
        specs.add(spec);
        return this;
    }

  
    public Specification<T> partialBuild(LogicalOperator op, SearchCriteria... criteria) {
        if (criteria.length == 0) {
            return null;
        }
 
        specs = new ArrayList<>();
        for (SearchCriteria param : criteria) {
            specs.add(new QuerySpecification<T>(param));
        }
 
        return joinSpecifications(op, specs);
    }
    
    public static <T> Specification<T> partialBuild(LogicalOperator op, List<SearchCriteria> criterias) {
        if (criterias.isEmpty()) {
            return null;
        }
 
        List<Specification<T>> specs = new ArrayList<>();
        for (SearchCriteria param : criterias) {
            specs.add(new QuerySpecification<T>(param));
        }
 
        
        return joinSpecifications(op, specs);
    }
    
    public static <T> Specification<T> joinSpecifications(LogicalOperator op, List<Specification<T>> specs) {
    	if (specs == null || specs.isEmpty()) {
    		return null;
    	}
        Specification<T> result = specs.get(0);
        if (op.equals(LogicalOperator.AND)) {
        	for (int i = 1; i < specs.size(); i++) {
                result = Specification.where(result).and(specs.get(i));
            }	
        } else if (op.equals(LogicalOperator.OR)) {
        	for (int i = 1; i < specs.size(); i++) {
                result = Specification.where(result).or(specs.get(i));
            }
        }
        
        return result;
    }
    
    public Specification<T> build() {
    	if (specs.isEmpty()) {
    		return null;
    	}
    	Specification<T> result = specs.get(0);
        for (Specification<T> specification : specs) {
            result = Specification.where(result).and(specification);
		}
        return result;
    }
    
}