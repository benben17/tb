package com.spacex.tb.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

    @Test
    void convertUtcJson2Obj() throws IOException {
        String str = "{\"utcDate\":\"2020-08-12T05:41:08.823Z\"}";

        AAA map = JsonUtils.convertUtcJson2Obj(str, AAA.class);
        System.out.println(JsonUtils.object2Json(map));
        System.out.println("----");
        Map map_2 = JsonUtils.convertUtcJson2Obj(str, Map.class);
        System.out.println(JsonUtils.object2Json(map_2));
    }

    private static class AAA {
        private Date utcDate;

        public Date getUtcDate() {
            return utcDate;
        }

        public void setUtcDate(Date utcDate) {
            this.utcDate = utcDate;
        }
    }

}