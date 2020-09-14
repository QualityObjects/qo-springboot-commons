package com.qualityobjects.springboot.services;

import com.qualityobjects.commons.exception.QOException;
import com.qualityobjects.springboot.dto.PageData;
import com.qualityobjects.springboot.dto.PageParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Interface que deben implementar todos los servicios que realicen paginación de entidades 
 */
public interface PaginationInterface<T> {

	public JpaSpecificationExecutor<T> getRepository();
		
	public default PageData<T> getPage(Pageable pageable, Specification<T> specs, PageParams params) throws QOException {
		Page<T> pageData = getRepository().findAll(specs, pageable);
		return new PageData<>(pageData,params);
	}
}
