package com.qualityobjects.springboot.entity;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;
import com.qualityobjects.commons.exception.QORuntimeException;
import com.qualityobjects.commons.utils.JsonUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DtoWrapper<T> {

    @Getter
    private final T bean;
    @JsonValue
    @JsonRawValue
    private final String jsonValue;

    public static <E> DtoWrapper<E> of(final E bean) {
        String json;
        try {
            json = JsonUtils.toJSON(bean);
            return new DtoWrapper<>(bean, json);
        } catch (IOException e) {
            log.error("Error serializing bean", e);
            throw new QORuntimeException("Error serializing bean: " + bean);
        }
    }

    public static <E> Iterable<DtoWrapper<E>> of(final Iterable<E> beans) {
        return StreamSupport.stream(beans.spliterator(), false).<DtoWrapper<E>>map(DtoWrapper::of)
                .collect(Collectors.toUnmodifiableList());
    }

    public static <E> DtoWrapper<E> of(final String jsonValue, Class<E> klass) {
        E bean;
        try {
            bean = JsonUtils.parseJSON(jsonValue, klass);
            return new DtoWrapper<>(bean, jsonValue);
        } catch (IOException e) {
            log.error("Error deserializing bean", e);
            throw new QORuntimeException("Error deserializing json: " + jsonValue);
        }
    }
}
