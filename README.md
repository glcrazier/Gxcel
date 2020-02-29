# Gxcel
An easy ORM tool of excel parser

Feel free to use, feel free to submit issues.

## Example Code

```

    @Data
    public static class ExcelCodeKey {
        @GxcelRowIndex(index = 0)
        private String code;

        @GxcelRowIndex(index = 1)
        private String key;
    }
    
    RowConfig<ExcelCodeKey> config = new RowConfig<>();
    config.setStartRow(0);
    config.setTargetSheet(0);
    config.setValidator(object -> StringUtils.isNoneBlank(object.getCode(), object.getKey()));
    
    List<ExcelCodeKey> parsedResult = Gxcel.parse(file.getInputStream(), ExcelCodeKey.class, config);
```

