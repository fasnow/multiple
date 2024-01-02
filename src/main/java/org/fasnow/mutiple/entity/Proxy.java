package org.fasnow.mutiple.entity;

public class Proxy{
    String host,port,username,password,type;
    boolean enable;

    public Proxy(String host, String port, String username, String password, boolean enable,String type) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.enable = enable;
        this.type = type;
    }

    public Proxy() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}

