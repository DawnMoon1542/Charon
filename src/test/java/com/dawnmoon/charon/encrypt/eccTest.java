package com.dawnmoon.charon.encrypt;

import com.dawnmoon.charon.util.CryptoUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class eccTest {

    @Test
    void genEccKeyPair() throws Exception {
        CryptoUtil.generateKeyPair();
    }
}
