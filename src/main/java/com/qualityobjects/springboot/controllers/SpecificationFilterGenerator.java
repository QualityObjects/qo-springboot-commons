package com.qualityobjects.springboot.controllers;

import com.qualityobjects.commons.exception.QOException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;

/**
 * Implementa el método para generar un objeto Specification a partir de un Map
 * 
 * @author rob
 */
public interface SpecificationFilterGenerator<T> {

	/**
	 * Monta el Specification filter con los datos pasados en la petición
	 * @param params
	 * @return Objecto Specification<T> usable en los repos mágicos de JPA
	 */
	public Specification<T> getSpecificationFilter(MultiValueMap<String, String> params) throws QOException;


}
