package test;

import java.net.InetSocketAddress;
import java.util.Date;

import javax.annotation.Resource;

import model.TestModel;
import model.TestModel.Req;
import model.TestModel.Resp;

import org.junit.Test;

import server.SimpleHttpServer;
import api.TestApi;

public class ATest extends AbstractTest {
    @Resource
    private TestApi testApi;

    @Test
    public void test() throws Exception {
        InetSocketAddress addr = new InetSocketAddress(8080);
        SimpleHttpServer server = new SimpleHttpServer(addr);
        server.start();
        Thread.sleep(2*1000);
        
        TestModel.Req req = new Req();
        req.setI(9);
        req.setL(2l);
        req.setS("你好");
        req.setId(2);
        req.setHeader1("aheader");
        req.setD(new Date());
        Resp test = testApi.test(req);
        System.err.println(test.getS());
        server.shutDown();
    }
}
