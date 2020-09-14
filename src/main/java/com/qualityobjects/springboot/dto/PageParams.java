package com.qualityobjects.springboot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
public class PageParams {

	@JsonProperty("page_size")
	int pageSize;
	@JsonProperty("page_number")
	int pageNumber;
	@JsonProperty("sortFields")
	List<String> sortFields;
	@JsonProperty("sortDir")
	Sort.Direction sortDirection;

	public PageParams(int pageNumber,int pageSize,List<String> sortFields,Sort.Direction sortDirection){
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.sortFields = sortFields;
		this.sortDirection = sortDirection;
	}
}
