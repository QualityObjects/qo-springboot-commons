package com.qualityobjects.springboot.controllers;

import com.qualityobjects.commons.exception.QOException;
import com.qualityobjects.springboot.common.PaginationUtils;
import com.qualityobjects.springboot.dto.PageData;
import com.qualityobjects.springboot.dto.PageParams;
import com.qualityobjects.springboot.services.PaginationInterface;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface IPaginationController<T> extends SpecificationFilterGenerator<T> {

	public PaginationInterface<T> getService();

	public PaginationUtils getPaginationUtils();
	
	/**
	 * 
	 * @param pageNumber
	 * @param pageSize
	 * @param sortFields
	 * @param sortDirection
	 * @return
	 * @throws QOException
	 */
	@GetMapping(path = "/page")
	public default PageData<T> getPage(@RequestParam("_page") Integer pageNumber,
									   @RequestParam("_pageSize") Integer pageSize,
									   @RequestParam("_sortFields") List<String> sortFields,
									   @RequestParam("_sortDir") Sort.Direction sortDirection,
									   @RequestParam MultiValueMap<String, String> filterParams) throws QOException {

		Specification<T> specs = this.getSpecificationFilter(filterParams);
		Pageable pageConfig = getPaginationUtils().generatePageableRequest(sortFields, sortDirection, pageNumber, pageSize);
		PageParams params = new PageParams(pageNumber,pageSize,sortFields,sortDirection);
		PageData<T> pageData = getService().getPage(pageConfig, specs,params);

		return pageData;
		
	}

}
