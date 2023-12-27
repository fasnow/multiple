package org.fasnow.mutiple.app.model.nacos;

import org.fasnow.mutiple.app.entity.Result;

public interface Vuls {
    Result CNVD_2020_67618(boolean addRootPath) throws Exception;
    Result CVE_2021_29441(boolean addRootPath) throws Exception;
    Result CNVD_2021_24491(boolean addRootPath) throws Exception;
    Result QVD_2023_6271(boolean addRootPath) throws Exception;
//    Result CNVD_2023_45001(String targetUrl) throws Exception;
}
