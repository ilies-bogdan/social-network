package com.socialnetwork.domain.validators;

import com.socialnetwork.exceptions.ValidationException;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}
