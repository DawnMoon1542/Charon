package com.dawnmoon.springboot_app_template.encrypt;

import com.dawnmoon.springboot_app_template.util.CryptoUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class eccTest {

    @Test
    void genEccKeyPair() throws Exception {
        CryptoUtil.generateKeyPair();
    }
}
