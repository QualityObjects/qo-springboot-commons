package com.qualityobjects.springboot.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * Clase con métodos de utilidad para la paginación de Spring Data
 */
@Slf4j
@Service
public class PaginationUtils {

    public Sort generateSort(List<String> sortFields, Sort.Direction sortDirection) {
    	if (ObjectUtils.isEmpty(sortFields)) {
    		return Sort.unsorted();
    	}
    	String[] arrSortFields = generateSortFields(sortFields);
    	if (sortDirection == null) {
    		sortDirection = Sort.DEFAULT_DIRECTION;
    	}
    	
    	return Sort.by(sortDirection, arrSortFields);
    }

    private String[] generateSortFields(List<String> sortFields) {
    	return sortFields.stream().<String>map(field -> {
        	if (field.contains("_")) 
        	{
                return Arrays.asList(field.split("_")).stream().reduce((str, part) -> 
                     str + StringUtils.capitalize(part)
                ).get();
            }
    		return field;
    	}).toArray(String[]::new);
	}

    public Pageable generatePageableRequest(List<String> sortFields, Sort.Direction order, Integer pageNumber, Integer pageSize) {
    	Sort sort = generateSort(sortFields, order);
		return generatePageableRequest(sort, pageNumber, pageSize);
    }

    public Pageable generatePageableRequest(Sort sort, Integer pageNumber, Integer pageSize) {
		return PageRequest.of(pageNumber - 1, pageSize, sort);
    }



	public Pageable generateNativePageableRequest(List<String> sortFields, Sort.Direction order, Integer pageNumber, Integer pageSize) {
		Sort sort = generateSortNative(sortFields, order);
		return generatePageableRequest(sort, pageNumber, pageSize);
	}

	public Sort generateSortNative(List<String> sortFields, Sort.Direction sortDirection) {
		if (ObjectUtils.isEmpty(sortFields)) {
			return Sort.unsorted();
		}
		String[] arrSortFields = generateNativeSortFields(sortFields);
		if (sortDirection == null) {
			sortDirection = Sort.DEFAULT_DIRECTION;
		}

		return Sort.by(sortDirection, arrSortFields);
	}

	private String[] generateNativeSortFields(List<String> sortFields) {
		return sortFields.stream().toArray(String[]::new);
	}

}