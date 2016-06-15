package test;

import javax.annotation.Resource;

import model.TestModel;
import model.TestModel.Req;
import model.TestModel.Resp;

import org.junit.Test;

import api.TestApi;

public class ATest extends AbstractTest {
    @Resource
    private TestApi testApi;

    @Test
    public void test() {
        TestModel.Req req = new Req();
        req.setI(9);
        req.setL(2l);
        req.setS("你好");
        req.setId(2);
        Resp test = testApi.test(req);
        Resp test2 = testApi.test(req);
        
        System.err.println(test.getS());
    }
}
