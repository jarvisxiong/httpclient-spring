/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package model;

import com.maoyan.machine.httpclient.spring.annotation.PathVariable;
import com.maoyan.machine.httpclient.spring.annotation.Request;
import com.maoyan.machine.httpclient.spring.annotation.Response;
import com.maoyan.machine.httpclient.spring.meta.Charset;
import com.maoyan.machine.httpclient.spring.meta.MimeType;

/**
 * @author xujia06
 * @created 2016年6月7日
 * 
 * @version 1.0
 */
public class TestModel {
    @Request(method = "post", mimeType = MimeType.JSON, charset = Charset.UTF_8, path = "/api/{id}/test.json")
    public static class Req {
        @PathVariable
        private Integer id;
        private String s;
        private Integer i;
        private Long l;

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public Integer getI() {
            return i;
        }

        public void setI(Integer i) {
            this.i = i;
        }

        public Long getL() {
            return l;
        }

        public void setL(Long l) {
            this.l = l;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    @Response(mimeType = MimeType.JSON)
    public static class Resp {
        private String s;
        private Integer i;
        private Long l;

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public Integer getI() {
            return i;
        }

        public void setI(Integer i) {
            this.i = i;
        }

        public Long getL() {
            return l;
        }

        public void setL(Long l) {
            this.l = l;
        }

    }
}
