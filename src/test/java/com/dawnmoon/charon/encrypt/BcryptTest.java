package com.dawnmoon.charon.encrypt;

import com.dawnmoon.charon.util.CryptoUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BcryptTest {

    @Test
    public void genBcryptResult() {
        String rawPassword1 = "qut@123456";
        String rawPassword2 = "qut@123456";
        String rawPassword3 = "qut@1234567";
        String bcryptResult1 = CryptoUtil.bcryptEncode(rawPassword1);
        String bcryptResult2 = CryptoUtil.bcryptEncode(rawPassword2);
        String bcryptResult3 = CryptoUtil.bcryptEncode(rawPassword3);
        System.out.println("rawPassword1=" + rawPassword1 + "\nrawPassword2=" + rawPassword2 + "\nrawPassword3=" + rawPassword3);
        System.out.println("bcryptResult1=" + bcryptResult1 + "\nbcryptResult2=" + bcryptResult2 + "\nbcryptResult3=" + bcryptResult3);
        System.out.println("pw1 = pw2? " + CryptoUtil.bcryptMatches(rawPassword1, bcryptResult1));
        System.out.println("pw1 = pw3? " + CryptoUtil.bcryptMatches(rawPassword1, bcryptResult3));
    }
}
