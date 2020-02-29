package org.glcrazier.gxcel;

import lombok.Data;

@Data
public class RowConfig<T> {

    private int startRow;

    private int targetSheet;

    private Validator<T> validator;

}
