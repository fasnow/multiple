package org.fasnow.mutiple;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void versionCompare() {
        System.out.println( Utils.versionCompare("1.0.1","1.0.2"));
    }
}