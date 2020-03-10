package org.glcrazier.gxcel.test;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.glcrazier.gxcel.Gxcel;
import org.glcrazier.gxcel.GxcelRowIndex;
import org.glcrazier.gxcel.RowConfig;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class GxcelTest {

    @Data
    public static class TestBean {

        @GxcelRowIndex(index = 2)
        private String code;

        @GxcelRowIndex(index = 6)
        private String userId;

        @GxcelRowIndex(index = 3)
        private String orderId;
    }

    @Test
    public void testFile() {
        InputStream fileStream = getClass().getClassLoader().getResourceAsStream("testCell.xlsx");
        RowConfig<TestBean> config = new RowConfig<>();
        config.setTargetSheet(0);
        config.setStartRow(2);
        config.setValidator(object -> StringUtils.isNoneBlank(object.code, object.orderId, object.userId));
        List<TestBean> result = Gxcel.parse(fileStream, TestBean.class, config);
        if (result == null) {
            System.out.println("result is null");
        } else {
            for (TestBean data : result) {
                System.out.println(data);
            }
        }
    }
}
