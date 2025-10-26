package com.dawnmoon.charon.util;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 * 加密工具类
 * 提供 BCrypt 和 ECC 加密功能
 */
@Slf4j
@Component
public class CryptoUtil {

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Value("${app.crypto.ecc.public-key:}")
    private String eccPublicKeyStr;

    @Value("${app.crypto.ecc.private-key:}")
    private String eccPrivateKeyStr;

    private PublicKey eccPublicKey;
    private PrivateKey eccPrivateKey;

    static {
        // 添加 BouncyCastle 提供者
        Security.addProvider(new BouncyCastleProvider());
    }

    @PostConstruct
    public void init() {
        try {
            if (eccPublicKeyStr != null && !eccPublicKeyStr.isEmpty()) {
                eccPublicKey = loadPublicKey(eccPublicKeyStr);
            } else {
                log.warn("ECC 公钥未配置，ECC 加密功能将不可用");
            }

            if (eccPrivateKeyStr != null && !eccPrivateKeyStr.isEmpty()) {
                eccPrivateKey = loadPrivateKey(eccPrivateKeyStr);
            } else {
                log.warn("ECC 私钥未配置，ECC 解密功能将不可用");
            }

            log.info("ECC 密钥对加载成功");
        } catch (Exception e) {
            log.error("初始化 ECC 密钥失败", e);
        }
    }

    // ==================== BCrypt 加密 ====================

    /**
     * BCrypt 加密
     */
    public static String bcryptEncode(String password) {
        return PASSWORD_ENCODER.encode(password);
    }

    /**
     * BCrypt 验证
     */
    public static boolean bcryptMatches(String rawPassword, String encodedPassword) {
        return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }

    // ==================== ECC 加密 ====================

    /**
     * ECC 加密
     */
    public String eccEncrypt(String data) throws Exception {
        if (eccPublicKey == null) {
            throw new IllegalStateException("ECC 公钥未配置");
        }

        Cipher cipher = Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, eccPublicKey);
        byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * ECC 解密
     */
    public String eccDecrypt(String cipherText) throws Exception {
        if (eccPrivateKey == null) {
            throw new IllegalStateException("ECC 私钥未配置");
        }

        Cipher cipher = Cipher.getInstance("ECIES", "BC");
        cipher.init(Cipher.DECRYPT_MODE, eccPrivateKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // ==================== 密钥加载 ====================

    private PublicKey loadPublicKey(String keyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
        return keyFactory.generatePublic(spec);
    }

    private PrivateKey loadPrivateKey(String keyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
        return keyFactory.generatePrivate(spec);
    }

    /**
     * 生成 ECC 密钥对（用于测试）
     * 注意：生产环境应使用专业工具生成并妥善保管密钥
     */
    public static void generateKeyPair() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "BC");
        keyGen.initialize(256);
        KeyPair keyPair = keyGen.generateKeyPair();

        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        System.out.println("ECC 公钥: " + publicKey);
        System.out.println("ECC 私钥: " + privateKey);
    }
}


