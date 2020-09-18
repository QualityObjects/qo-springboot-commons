package com.qualityobjects.springboot.services;

import com.qualityobjects.commons.exception.ElementNotFoundException;
import com.qualityobjects.commons.exception.QOException;
import com.qualityobjects.springboot.entity.DtoWrapper;
import com.qualityobjects.springboot.entity.EntityBase;
import com.qualityobjects.springboot.repository.BaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Interface que deben implementar todos los servicios que realicen operaciones CRUD (Create-Retrieve-Update-Delete) así como
 * los métodos necesarios para la paginación
 * 
 */
@Transactional( propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public interface CRUDInterface<T extends EntityBase<I>, I> {

	public String getEntityName();

	public BaseRepository<T, I> getRepository();
	public Integer getCurrentUser();

	public default void loadCreationFields(EntityBase<I> entity) throws QOException {
		if (entity.getCreatedBy() == null) {
			entity.setCreatedBy(getCurrentUser());
		}
		entity.setCreationTimestamp(LocalDateTime.now());
		loadModificationFields(entity);
	}

	public default void loadModificationFields(EntityBase<I> entity) throws QOException {
		if (entity.getModifiedBy() == null) {
			entity.setModifiedBy(getCurrentUser());
		}
		entity.setModificationTimestamp(LocalDateTime.now());
	}
	
	public default DtoWrapper<T> getById(I id) throws QOException {
		DtoWrapper<T> dtoWrapper = DtoWrapper.of(getRepository().findById(id).orElse(null));
		
		if (dtoWrapper == null) {
			throw new ElementNotFoundException(getEntityName(), id);
		}
		return dtoWrapper;
	}
 
	public default DtoWrapper<T> create(T element) throws QOException {
		loadCreationFields(element);
		DtoWrapper<T> dtoWrapper = DtoWrapper.of(getRepository().saveAndFlush(element));
		return dtoWrapper;
	}

	public default DtoWrapper<T> update(T element) throws QOException {
		DtoWrapper<T> currentDto = this.getById(element.getId());
		T current = currentDto.getBean();
		element.setCreationTimestamp(current.getCreationTimestamp());
		element.setCreatedBy(current.getCreatedBy());
		loadModificationFields(element);
		DtoWrapper<T> dtoWrapper = DtoWrapper.of(getRepository().saveAndFlush(element));
		return dtoWrapper;
	}

	public default void delete(I id) throws QOException {
		getRepository().deleteById(id);
	}


	public default Iterable<DtoWrapper<T>> findAll() throws QOException {
		Iterable<DtoWrapper<T>>  dtoWrapperIterable = DtoWrapper.of(getRepository().findAll());
		return dtoWrapperIterable;
	}

	public default Long count(Specification<T> specs) throws QOException {
		return getRepository().count(specs);
	}

	public default Iterable<DtoWrapper<T>> findAll(Specification<T> specs) throws QOException {
		Iterable<DtoWrapper<T>>  dtoWrapperIterable = this.findAll(specs, Sort.unsorted());
		return dtoWrapperIterable;
	}

	public default Iterable<DtoWrapper<T>> findAll(Specification<T> specs, Sort sort) throws QOException {
		if (sort ==  null) {
			Iterable<DtoWrapper<T>>  dtoWrapperIterable = DtoWrapper.of(getRepository().findAll(specs));
			return dtoWrapperIterable;
		} else {
			Iterable<DtoWrapper<T>>  dtoWrapperIterable = DtoWrapper.of(getRepository().findAll(specs, sort));
			return dtoWrapperIterable;
		}
	}	

}
