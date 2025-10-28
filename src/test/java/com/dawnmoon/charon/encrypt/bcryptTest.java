package com.dawnmoon.charon.encrypt;

import com.dawnmoon.charon.util.CryptoUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class bcryptTest {

    @Test
    public void genBcryptResult() {
        String rawPassword = "qut@123456";
        String bcryptResult = CryptoUtil.bcryptEncode(rawPassword);
        System.out.println("rawPassword=" + rawPassword + "\nbcryptResult=" + bcryptResult);
    }
}
