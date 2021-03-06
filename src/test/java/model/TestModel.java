/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package model;

import java.util.Date;

import com.maoyan.machine.httpclient.spring.annotation.Header;
import com.maoyan.machine.httpclient.spring.annotation.PathVariable;
import com.maoyan.machine.httpclient.spring.annotation.Request;
import com.maoyan.machine.httpclient.spring.annotation.Response;
import com.maoyan.machine.httpclient.spring.meta.Charsets;
import com.maoyan.machine.httpclient.spring.meta.MimeTypes;

/**
 * @author xujia06
 * @created 2016年6月7日
 * 
 * @version 1.0
 */
public class TestModel {
    @Request(method = "post", mimeType = MimeTypes.JSON, charset = Charsets.UTF_8, path = "/api/{id}")
    public static class Req {
        @PathVariable
        private Integer id;
        private String s;
        private Integer i;
        private Long l;
        private Date d;
        @Header("HEADER")
        private String header1;

        public String getHeader1() {
            return header1;
        }
        
        public Date getD() {
            return d;
        }

        public void setD(Date d) {
            this.d = d;
        }

        public void setHeader1(String header1) {
            this.header1 = header1;
        }

        public String getS() {
            return s;
        }

        public Req setS(String s) {
            this.s = s;
            return this;
        }

        public Integer getI() {
            return i;
        }

        public Req setI(Integer i) {
            this.i = i;
            return this;
        }

        public Long getL() {
            return l;
        }

        public Req setL(Long l) {
            this.l = l;
            return this;
        }

        public Integer getId() {
            return id;
        }

        public Req setId(Integer id) {
            this.id = id;
            return this;
        }
    }

    @Response
    public static class Resp extends Req{

    }
}
