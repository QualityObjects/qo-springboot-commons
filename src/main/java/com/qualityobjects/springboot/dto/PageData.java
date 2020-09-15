package com.qualityobjects.springboot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qualityobjects.springboot.entity.DtoWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
@JsonInclude(content = JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor(staticName = "of")
public class PageData<T> {
	Iterable<DtoWrapper<T>> content;
	long total;
	PageParams params;

	public static <T> PageData<T> of(Page<T> page, PageParams params) {
		return PageData.of(DtoWrapper.of(page.getContent()), page.getTotalElements(), params);
	}

	public static <T> PageData<T> of(Page<T> page) {
		return PageData.of(DtoWrapper.of(page.getContent()), page.getTotalElements(), null);
	}
}

