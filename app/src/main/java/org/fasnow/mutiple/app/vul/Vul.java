package org.fasnow.mutiple.app.vul;

import okhttp3.Headers;
import okhttp3.RequestBody;

public class Vul {
    private String name;
    private String vulId;
    private String description;

    private String affectedVersion;

    private String refUrl;

    private String method;

    private String path;//  /nacos/v1/auth/users?pageNo=1&pageSize=1

    private  okhttp3.Headers headers;

    private  okhttp3.RequestBody body;

    private String flag;

    public Vul(String name, String vulId, String affectedVersion, String description, String refUrl) {
        this.name = name;
        this.vulId = vulId;
        this.description = description;
        this.affectedVersion = affectedVersion;
        this.refUrl = refUrl;
    }

    public Vul() {
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public RequestBody getBody() {
        return body;
    }

    public void setBody(RequestBody body) {
        this.body = body;
    }

    public String getRefUrl() {
        return refUrl;
    }

    public void setRefUrl(String refUrl) {
        this.refUrl = refUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVulId() {
        return vulId;
    }

    public void setVulId(String vulId) {
        this.vulId = vulId;
    }

    public String getAffectedVersion() {
        return affectedVersion;
    }

    public void setAffectedVersion(String affectedVersion) {
        this.affectedVersion = affectedVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
