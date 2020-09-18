package com.qualityobjects.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, I> extends CrudRepository<T, I>, JpaSpecificationExecutor<T>, JpaRepository<T,I> {

}
