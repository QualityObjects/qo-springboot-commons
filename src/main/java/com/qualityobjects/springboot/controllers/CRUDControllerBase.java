package com.qualityobjects.springboot.controllers;

import com.qualityobjects.commons.exception.InvalidInputDataException;
import com.qualityobjects.commons.exception.QOException;
import com.qualityobjects.springboot.entity.EntityBase;
import com.qualityobjects.springboot.services.CRUDInterface;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Implementamos los métodos CRUD sobre una entidad
 * 
 * @author rob
 */
public interface CRUDControllerBase<T extends EntityBase<I>, I> extends SpecificationFilterGenerator<T> {

	public CRUDInterface<T, I> getService();

	@GetMapping(path = {"/", ""})
	public default Iterable<T> list(@RequestParam MultiValueMap<String, String> filterParams) throws QOException {
		Specification<T> specs = this.getSpecificationFilter(filterParams);
		Sort sort = this.getSort(filterParams);
		return getService().findAll(specs, sort);
	}

	@GetMapping(path = {"/count"})
	public default Long count(@RequestParam MultiValueMap<String, String> filterParams) throws QOException {
		Specification<T> specs = this.getSpecificationFilter(filterParams);
		return getService().count(specs);
	}

	private Sort getSort(MultiValueMap<String, String> params) {
		if (!params.isEmpty()) {
			String sortField = params.getFirst("_sortField");
			if (!StringUtils.isEmpty(sortField)) {
				String dir = params.getFirst("_sortDir");
				if (dir == null) {
					return Sort.by(sortField);
				} else {
					Direction direction = Direction.fromString(dir);
					return Sort.by(direction, sortField);
				}
			}
		}
		return null;
	}
	
	/**
	 * Usamos Optional para evitar vulneravilidad detectada por Sonar: https://rules.sonarsource.com/java/tag/spring/RSPEC-4684
	 * @param param
	 * @return
	 * @throws QOException
	 */
	@PostMapping(path = {"/", ""})
	public default T create(@RequestBody Optional<T> param) throws QOException {
		T element = param.orElse(null);
		if (element == null) {
			throw new InvalidInputDataException();
		}
		return getService().create(element);
	}

	@GetMapping(path = "/{id}")
	public default T findOne(@PathVariable("id") I idElement) throws QOException {
		return getService().getById(idElement);
	}

	@PostMapping(path = "/{id}")
	public default T update(@PathVariable("id") I idElement, @RequestBody T element) throws QOException {
		return getService().update(element);
	}

	@DeleteMapping(path = "/{id}")
	public default void delete(@PathVariable("id") I idElement) throws QOException {
		getService().delete(idElement);
	}

}
