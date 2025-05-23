package com.example.teamcity.api.generators;

import com.example.teamcity.api.annotations.Optional;
import com.example.teamcity.api.annotations.Parameterizable;
import com.example.teamcity.api.annotations.Random;
import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.models.TestData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class TestDataGenerator {

    private TestDataGenerator() {
    }

    /**
     * The main method for generating test data.

     * If the field has the Optional annotation, it is skipped, otherwise:

     * 1) if the field has the Parameterizable annotation, and parameters were passed to the method, then the passed parameters are set one by one
     * (as fields with this annotation are encountered). That is, if during generation 4 fields with the Parameterizable
     * annotation were passed, but 3 parameters were passed to the method, then the values will be set only for the first
     * three elements encountered in the order they were passed to the method. Therefore, it is also important to monitor the order of the fields
     * in the @Data class;

     * 2) otherwise, if the field has the Random annotation and it is a string, it is filled with random data;

     * 3) otherwise, if the field is an inheritor of the BaseModel class, then it is generated, recursively sending it to the new generate method;

     * 4) otherwise, if the field is a List whose generic type is an inheritor of the BaseModel class, then it is set by a list of
     * one element, which is generated by recursively sending it to the new generate method.
     * The generatedModels parameter is passed when several entities are generated in a cycle, and contains the entities generated in the previous steps.
     * Allows you to set it when generating a complex entity that contains another entity generated in the previous step by its field, rather than generating a new one.
     * This logic applies only to points 3 and 4. For example, if NewProjectDescription was generated, then passing it
     * as the generatedModels parameter when generating BuildType will reuse it when setting the
     * NewProjectDescription project field, instead of generating a new one.
     */
    public static <T extends BaseModel> T generate(List<BaseModel> generatedModels, Class<T> generatorClass,
                                                   Object... parameters) {
        try {
            var instance = generatorClass.getDeclaredConstructor().newInstance();
            for (var field : generatorClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (!field.isAnnotationPresent(Optional.class)) {
                    var generatedClass = generatedModels.stream().filter(m
                            -> m.getClass().equals(field.getType())).findFirst();
                    if (field.isAnnotationPresent(Parameterizable.class) && parameters.length > 0) {
                        field.set(instance, parameters[0]);
                        parameters = Arrays.copyOfRange(parameters, 1, parameters.length);
                    } else if (field.isAnnotationPresent(Random.class)) {
                        if (String.class.equals(field.getType())) {
                            field.set(instance, RandomData.getString());
                        }
                    } else if (BaseModel.class.isAssignableFrom(field.getType())) {
                        var finalParameters = parameters;
                        field.set(instance, generatedClass.orElseGet(() -> generate(
                                generatedModels, field.getType().asSubclass(BaseModel.class), finalParameters)));
                    } else if (List.class.isAssignableFrom(field.getType())) {
                        if (field.getGenericType() instanceof ParameterizedType pt) {
                            var typeClass = (Class<?>) pt.getActualTypeArguments()[0];
                            if (BaseModel.class.isAssignableFrom(typeClass)) {
                                var finalParameters = parameters;
                                field.set(instance, generatedClass.map(List::of).orElseGet(() -> List.of(generate(
                                        generatedModels, typeClass.asSubclass(BaseModel.class), finalParameters))));
                            }
                        }
                    }
                }
                field.setAccessible(false);
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                 | NoSuchMethodException e) {
            throw new IllegalStateException("Cannot generate test data", e);
        }
    }

    public static <T extends BaseModel> T generate(Class<T> generatorClass, Object... parameters) {
        return generate(Collections.emptyList(), generatorClass, parameters);
    }

    public static TestData generate() {
        try {
            var instance = TestData.class.getDeclaredConstructor().newInstance();
            var generatedModels = new ArrayList<BaseModel>();
            for (var field : TestData.class.getDeclaredFields()) {
                field.setAccessible(true);
                if (BaseModel.class.isAssignableFrom(field.getType())) {
                    var generatedModel = generate(generatedModels, field.getType().asSubclass(BaseModel.class));
                    field.set(instance, generatedModel);
                    generatedModels.add(generatedModel);
                }
                field.setAccessible(false);
            }
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IllegalStateException("Cannot generate test data", e);
        }
    }
}