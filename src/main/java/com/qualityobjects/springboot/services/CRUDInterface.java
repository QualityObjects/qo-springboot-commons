package com.qualityobjects.springboot.services;

import com.qualityobjects.commons.exception.ElementNotFoundException;
import com.qualityobjects.commons.exception.QOException;
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
	
	public default T getById(I id) throws QOException {
		T ar = getRepository().findById(id).orElse(null);
		
		if (ar == null) { 
			throw new ElementNotFoundException(getEntityName(), id);
		}
		return ar;
	}
 
	public default T create(T element) throws QOException {
		loadCreationFields(element);
		return getRepository().save(element); 
	}

	public default T update(T element) throws QOException {
		T current = this.getById(element.getId());
		element.setCreationTimestamp(current.getCreationTimestamp());
		element.setCreatedBy(current.getCreatedBy());
		loadModificationFields(element);
		return getRepository().save(element); 
	}

	public default void delete(I id) throws QOException {
		getRepository().deleteById(id);
	}


	public default Iterable<T> findAll() throws QOException {
		return getRepository().findAll();
	}

	public default Long count(Specification<T> specs) throws QOException {
		return getRepository().count(specs);
	}

	public default Iterable<T> findAll(Specification<T> specs) throws QOException {
		return this.findAll(specs, Sort.unsorted());
	}

	public default Iterable<T> findAll(Specification<T> specs, Sort sort) throws QOException {
		if (sort ==  null) {
			return getRepository().findAll(specs);
		} else {
			return getRepository().findAll(specs, sort);
		}
	}	

}
