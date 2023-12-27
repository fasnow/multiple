package org.fasnow.mutiple.app;


import org.fasnow.mutiple.app.entity.Proxy;

public class Config {
    private static final Config instance = new Config();
    private static final Proxy proxy = new Proxy("127.0.0.1","8080","","",false,"HTTP");
    private Config() {}
    public static Config getConfig() {
        return instance;
    }

    public void setProxy(Proxy proxy){
        proxy.setHost(proxy.getHost());
        proxy.setPort(proxy.getPort());
        proxy.setUsername(proxy.getUsername());
        proxy.setPassword(proxy.getPassword());
        proxy.setEnable(proxy.isEnable());
        proxy.setType(proxy.getType());
    }

    public Proxy getProxy(){
        return proxy;
    }
}

