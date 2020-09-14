package com.qualityobjects.springboot.services;

import com.qualityobjects.commons.exception.QOException;
import com.qualityobjects.springboot.dto.CatalogRow;

import java.util.List;

/**
 * Interface que deben implementar todos los repositorios de entidades que sirvan como catálogo (usadas en combos)
 * 
 */
public interface CatalogQuery {

	public List<CatalogRow> getCatalogList() throws QOException;

}
