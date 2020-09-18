package com.qualityobjects.springboot.controllers;

import com.qualityobjects.springboot.specifications.FilterOperator;
import com.qualityobjects.springboot.specifications.LogicalOperator;
import com.qualityobjects.springboot.specifications.QuerySpecificationsBuilder;
import com.qualityobjects.springboot.specifications.SearchCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class SpecsFilterUtils {

    private SpecsFilterUtils() {
        super();
    }


    // ADD STRING LIKE
    public static void addStringLike(String param, String dbField, QuerySpecificationsBuilder<?> builder) {
		if (!StringUtils.isEmpty(param)) {
			builder.with(dbField, FilterOperator.LIKE, param);
		}
    }

    // ADD DATE RANGE
	public static void addDateRange(String paramMin,String paramMax, String dbField, QuerySpecificationsBuilder<?> builder) {

		if (!StringUtils.isEmpty(paramMin)) {
		   LocalDate initDate = LocalDate.parse(paramMin);
			 builder.with(dbField, FilterOperator.GREATER_THAN_OR_EQUAL, initDate);
		 }
		 if (!StringUtils.isEmpty(paramMax)) {
			 LocalDate endDate =  LocalDate.parse(paramMax);
			 builder.with(dbField, FilterOperator.LOWER_THAN_OR_EQUAL, endDate);
		 }
	}

	public static void addDateTimeRange(String paramMin,String paramMax, String dbField, QuerySpecificationsBuilder<?> builder) {

		if (!StringUtils.isEmpty(paramMin)) {
			 builder.with(dbField, FilterOperator.GREATER_THAN_OR_EQUAL, parseLocalDateTime(paramMin));
		}
		if (!StringUtils.isEmpty(paramMax)) {
			builder.with(dbField, FilterOperator.LOWER_THAN_OR_EQUAL, parseLocalDateTime(paramMax));
		}
	}

	private static LocalDateTime parseLocalDateTime(String dateTimeStr) {

		if (dateTimeStr.length() <= 10) {
			return LocalDate.parse(dateTimeStr).atTime(0, 0);
		} else {
			return LocalDateTime.parse(dateTimeStr);
		}
	}

	public static void addDateEquals(String paramDate, String dbField, QuerySpecificationsBuilder<?> builder) {

		if (!StringUtils.isEmpty(paramDate)) {
		   LocalDate date = LocalDate.parse(paramDate);
			 builder.with(dbField, FilterOperator.EQUAL, date);
		 }
	}

    // ADD DATE RANGE IN RANGE
    public static <T>  void addDateRangeInRange(String paramMin, String paramMax, String dbFieldMin, String dbFieldMax, QuerySpecificationsBuilder<?> builder) {

		if(!StringUtils.isEmpty(paramMin) || !StringUtils.isEmpty(paramMax)) {
			List<SearchCriteria> criteriasOr = new ArrayList<>();

			if (!StringUtils.isEmpty(paramMin)) {
				LocalDate initDate = LocalDate.parse(paramMin);

				criteriasOr.add(new SearchCriteria(dbFieldMax, FilterOperator.GREATER_THAN_OR_EQUAL, initDate));
			}

			if (!StringUtils.isEmpty(paramMax)){
				LocalDate endDate =  LocalDate.parse(paramMax);
				criteriasOr.add(new SearchCriteria(dbFieldMin, FilterOperator.LOWER_THAN_OR_EQUAL, endDate));
			}
			builder.with(LogicalOperator.AND,criteriasOr);

		}

    }

    // ADD INTEGER RANGE
     public static void addIntegerRange(String paramMin, String paramMax, String dbField, QuerySpecificationsBuilder<?> builder) {

    	 if (!StringUtils.isEmpty(paramMin)) {
   			builder.with(dbField, FilterOperator.GREATER_THAN_OR_EQUAL, Integer.parseInt(paramMin));
   		}
   		if (!StringUtils.isEmpty(paramMax)) {
   			builder.with(dbField, FilterOperator.LOWER_THAN_OR_EQUAL, Integer.parseInt(paramMax));
   		}
     }

    // ADD STRING IN
    public static void addStringIn(List<String> params, String dbField, QuerySpecificationsBuilder<?> builder) {
        if (params != null && !params.isEmpty()) {
			builder.with(dbField, FilterOperator.IN, params);
		}
    }

    // ADD INTEGER IN
    public static void addIntegerIn(List<String> params, String dbField, QuerySpecificationsBuilder<?> builder) {
       if (!ObjectUtils.isEmpty(params)) {
			builder.with(dbField, FilterOperator.IN, params.parallelStream().map(Integer::parseInt).collect(Collectors.toList()));
		}
    }

    // ADD INTEGER EQUALS
    public static void addIntegerEquals(String param, String dbField, QuerySpecificationsBuilder<?> builder) {
        if (!StringUtils.isEmpty(param)) {
			builder.with(dbField, FilterOperator.EQUAL, Integer.parseInt(param));
		}
    }

    // ADD DOUBLE EQUALS
    public static void addDoubleEquals(String param, String dbField, QuerySpecificationsBuilder<?> builder) {
        if (!StringUtils.isEmpty(param)) {
			builder.with(dbField, FilterOperator.EQUAL, Double.parseDouble(param));
		}
    }

    // ADD STRING EQUALS
    public static void addStringEquals(String param, String dbField, QuerySpecificationsBuilder<?> builder) {
        if (!StringUtils.isEmpty(param)) {
			builder.with(dbField, FilterOperator.EQUAL, param);
		}
    }

    // ADD BOOLEAN EQUALS
    public static void addBooleanEquals(String param, String dbField, QuerySpecificationsBuilder<?> builder) {
        if (!StringUtils.isEmpty(param)) {
            builder.with(dbField, FilterOperator.EQUAL, Boolean.valueOf(param));
        }
    }

    //METODO IS NULL
    public static <T> void addStringIsNull(String field, QuerySpecificationsBuilder<T> builder) {

		Specification<T> partialSpecs = builder.partialBuild(LogicalOperator.OR,
				new SearchCriteria(field, FilterOperator.IS_NULL, null));
		builder.with(partialSpecs);
    }
}
