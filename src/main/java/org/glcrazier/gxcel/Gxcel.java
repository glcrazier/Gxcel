package org.glcrazier.gxcel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;

public class Gxcel {

    private static Map<Integer, Method> parseMethods(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        HashMap<Integer, Method> setterMap = new HashMap<>();
        for (Field field : fields) {
            GxcelRowIndex annotation = field.getAnnotation(GxcelRowIndex.class);
            if (annotation == null) {
                continue;
            }
            Method method = ReflectUtil.getSetter(clazz, field);
            if (method == null) {
                continue;
            }
            int rowIndex = annotation.index();
            if (rowIndex < 0) {
                throw new IllegalArgumentException("Row index must be positive.");
            }
            if (setterMap.containsKey(rowIndex)) {
                throw new DuplicateRowIndexException(rowIndex + " already in use.");
            }
            setterMap.put(rowIndex, method);

        }
        return setterMap;
    }

    private static <T> List<T> parse(XSSFWorkbook workbook, Class<T> clazz, RowConfig<T> config) {
        Constructor<T> constructor;
        try {
            constructor = clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            return null;
        }
        Map<Integer, Method> methods = parseMethods(clazz);
        if (methods.isEmpty()) {
            return null;
        }
        if (config == null) {
            return null;
        }
        XSSFSheet sheet = workbook.getSheetAt(config.getTargetSheet());
        Iterator<Row> iterator = sheet.rowIterator();
        int skipCount = 0;
        List<T> result = new ArrayList<>();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            if (skipCount < config.getStartRow()) {
                skipCount++;
                continue;
            }
            T object;
            try {
                object = constructor.newInstance();
            } catch (Exception e) {
                //
                continue;
            }
            for (int i = row.getFirstCellNum(); i <= row.getLastCellNum(); i++) {
                Method method = methods.get(i);
                if (method == null) {
                    continue;
                }
                Class<?> valueType = method.getParameterTypes()[0];
                Cell cell = row.getCell(i);
                if (cell == null) {
                    break;
                }
                if (valueType == String.class) {
                    CellType cellType = cell.getCellType();
                    String value = null;
                    if (cellType == CellType.STRING) {
                        value = cell.getStringCellValue();
                    } else if (cellType == CellType.NUMERIC) {
                        value = new DecimalFormat("#").format(cell.getNumericCellValue());
                    }
                    try {
                        method.invoke(object, value);
                    } catch (Exception e) {
                        //
                    }
                }
            }
            Validator<T> validator = config.getValidator();
            if (validator == null || validator.validate(object)) {
                result.add(object);
            }

        }
        return result;
    }

    public static <T> List<T> parse(InputStream inputStream, Class<T> clazz, RowConfig<T> config) {
        XSSFWorkbook workbook;
        try {
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            //
            return null;
        }
        try {
            return parse(workbook, clazz, config);
        } catch (Exception e) {
            //
            return null;
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                //
            }
        }
    }

}
