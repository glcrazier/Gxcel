package org.glcrazier.gxcel;

public interface Validator<T> {
    boolean validate(T object);
}
