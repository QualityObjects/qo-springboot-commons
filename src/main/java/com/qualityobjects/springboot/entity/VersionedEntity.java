package com.qualityobjects.springboot.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@MappedSuperclass
public class VersionedEntity {

	@JsonProperty("row_version")
	@Column(name="row_version")
	@Version
	@Getter @Setter
	Integer version = 0;
}
