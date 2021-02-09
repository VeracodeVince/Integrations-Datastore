package com.checkmarx.integrations.datastore.utils;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.stream.Collectors;

public class ObjectMapperUtil {
    private static final ModelMapper modelMapper = getModelMapper();

    static ModelMapper getModelMapper() {
        ModelMapper result = new ModelMapper();

        // Without this setting, modelMapper may show unexpected behavior.
        // E.g. trying to map SCMCreateDto.clientId to Scm.id, which doesn't make sense.
        result.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        return result;
    }

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