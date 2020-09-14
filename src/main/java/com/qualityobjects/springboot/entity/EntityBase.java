package com.qualityobjects.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false )
@MappedSuperclass
public abstract class EntityBase<I> extends VersionedEntity {
	
	public abstract I getId();
	
    @Column(name= "creation_timestamp", updatable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("creation_timestamp")
    @CreationTimestamp
    private LocalDateTime creationTimestamp;

    @Column(name= "modification_timestamp")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonProperty("modification_timestamp")
    @UpdateTimestamp
    private LocalDateTime modificationTimestamp;

    /**
     * Id del empleado que creo el registro en BBDD
     * Intencionadamente no se hace el mapeo con JPA para optimizar tiempos de escritura.
     */
    @Column(name= "created_by", updatable = false)
    @JsonProperty("created_by")
    private Integer createdBy;
    
    /**
     * Id del último empleado que modificó el registro en BBDD
     * Intencionadamente no se hace el mapeo con JPA para optimizar tiempos de escritura.
     */
    @Column(name= "modified_by")
    @JsonProperty("modified_by")
    private Integer modifiedBy;
    
    
}
