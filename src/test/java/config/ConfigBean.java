package config;

import com.maoyan.machine.httpclient.spring.meta.DynamicBaseUrl;

public class ConfigBean implements DynamicBaseUrl{
    private int i = 1;
    public String getBaseUrl() {
        String result;
        if(i == 1) {
            result =  "http://localhost:8080";
        }else {
            result =  "";
        }
        i++;
        return result;
    }
}
