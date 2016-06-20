/*
 * Copyright (c) 2010-2015 meituan.com
 * All rights reserved.
 * 
 */
package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.maoyan.machine.httpclient.spring.meta.MimeTypes;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * TODO 在这里编写类的功能描述
 *
 * @author xujia06
 * @created 2016年6月17日
 *
 * @version 1.0
 */
@SuppressWarnings("restriction")
public class SimpleHttpServer {
    private HttpServer server;
    
    public SimpleHttpServer(InetSocketAddress addr) throws IOException {
        server = HttpServer.create(addr, 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(Executors.newCachedThreadPool());
    }

    public void start(){
        server.start();
        System.out.println("Server started");
    }
    
    public void shutDown() {
        server.stop(0);
        System.out.println("Server stoped");
    }
    
    class MyHandler implements HttpHandler {
        
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            if (!(requestMethod.equalsIgnoreCase("POST") || requestMethod.equalsIgnoreCase("PUT"))) {
                returnErr(exchange, String.format("只支持POST、PUT方法,当前方法：%s", requestMethod));
                return;
            }
            
            String mimeType = exchange.getRequestHeaders().get("Content-Type").get(0).split(";")[0];
            if (!mimeType.equals(MimeTypes.JSON)) {
                returnErr(exchange, String.format("只支持JSO格式的请求,当前格式：%s", mimeType));
            }
            
            exchange.getResponseHeaders().putAll(exchange.getRequestHeaders());
            exchange.sendResponseHeaders(200, 0);
            
            InputStream requestBody = exchange.getRequestBody();
            OutputStream responseBody = exchange.getResponseBody();
            byte[] buf = new byte[1024];
            int read = -1;
            while((read = requestBody.read(buf)) != -1){
                responseBody.write(buf, 0, read);
            }
            responseBody.close();
        }
        
        private void returnErr(HttpExchange exchange, String err) throws IOException{
            exchange.getResponseHeaders().set("Content-Type", "text/plain");
            exchange.sendResponseHeaders(500, 0);
            exchange.getResponseBody().write(err.getBytes("UTF-8"));
            exchange.close();
        }
    }
}
