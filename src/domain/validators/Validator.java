package domain.validators;

import exceptions.ValidationException;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}
