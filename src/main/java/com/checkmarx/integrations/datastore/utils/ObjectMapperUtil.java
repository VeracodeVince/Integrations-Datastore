package com.checkmarx.integrations.datastore.utils;

import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class ObjectMapperUtil {

    private static ModelMapper modelMapper = new ModelMapper();

    private ObjectMapperUtil() {
    }

    public static <S, T> List<T> mapList(List<S> source, Class<T> destination) {
        return source
                .stream()
                .map(element -> modelMapper.map(element, destination))
                .collect(Collectors.toList());
    }

    public static <D, T> D map(final T source, Class<D> destination) {
        return modelMapper.map(source, destination);
    }
}