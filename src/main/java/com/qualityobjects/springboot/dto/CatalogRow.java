package com.qualityobjects.springboot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Interface (Projection para Spring Data) con elementos retornados para usar en combos y similares
 * 
 */
@JsonInclude(Include.NON_NULL)
public interface CatalogRow {

	public Integer getId();

	public String getCode();

	public String getName();
	
	public String getLabel();
	
}
