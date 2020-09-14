package com.qualityobjects.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor(staticName = "of")
public class PageData<T> {

	List<T> content;
	long total;
	com.qualityobjects.springboot.dto.PageParams params;
	public PageData() {
	}

	public PageData(Page<T> page, com.qualityobjects.springboot.dto.PageParams params) {
		this.content = page.getContent();
		this.total = page.getTotalElements();
		this.params = params;
	}

	public void setParams(com.qualityobjects.springboot.dto.PageParams pageParams) {
		this.params = pageParams;
	}
}
