/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.wallet.pass.passsdk;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The type Pass file service test util.
 */
public class PassFileServiceTestUtil {

    /**
     * The constant TEST_PASS_TYPE.
     */
    public static final String TEST_PASS_TYPE = "Your service ID";

    private Properties properties = new Properties();

    private PassUtilService utilService = new PassUtilServiceImpl();

    /**
     * Gets properties.
     *
     * @return the properties
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Create fixed pass byte [ ].
     *
     * @param passType the pass type
     * @param passFileService the pass file service
     * @return the byte [ ]
     * @throws PassException the pass exception
     */
    public byte[] createFixedPass(String passType, PassFileService passFileService) throws PassException {
        String passNumber = "100";
        ObjectNode passJson = JsonNodeFactory.instance.objectNode();
        passJson.put(PassFileServiceImpl.PASS_PASSTYPEIDENTIFIER, passType);
        passJson.put(PassFileServiceImpl.PASS_SERIALNUMBER, passNumber);
        passJson.put(PassFileServiceImpl.PASS_WEBSERVICEURL, "http://www.test.com");
        passJson.put(PassFileServiceImpl.PASS_AUTHORIZATIONTOKEN, "token");
        int fileCount = new SecureRandom().nextInt(10);

        Map<String, byte[]> fileMap = new HashMap<String, byte[]>();

        for (int i = 0; i < fileCount; i++) {
            String fileName = utilService.getCompressUUID();
            String fileContent = utilService.getCompressUUID();
            fileMap.put(fileName, fileContent.getBytes(StandardCharsets.UTF_8));
        }

        byte[] signBytes = passFileService.createPass(fileMap, passJson.toString());
        return signBytes;

    }

    /**
     * Test create sign message.
     *
     * @param passType the pass type
     * @param passFileService the pass file service
     * @throws PassException the pass exception
     */
    public void testCreateSignMessage(String passType, PassFileService passFileService) throws PassException {
        ObjectNode messageJson = JsonNodeFactory.instance.objectNode();
        int fileCount = new SecureRandom().nextInt(10);

        for (int i = 0; i < fileCount + 5; i++) {
            messageJson.put("key_" + i, "value_" + utilService.getCompressUUID());
        }

        byte[] messageBytes = messageJson.toString().getBytes(StandardCharsets.UTF_8);
        byte[] signMessage = passFileService.signMessage(messageBytes, passType);
        String signByteStr = Base64.encodeBase64String(signMessage);
        System.out.println(signByteStr);
    }

    /**
     * Test create pass.
     *
     * @param passType the pass type
     * @param passFileService the pass file service
     * @throws PassException the pass exception
     */
    public void testCreatePassFile(String passType, PassFileService passFileService) throws IOException, PassException {
        byte[] randomPass = createFixedPass(passType, passFileService);
        Path signFilePath = Paths.get("target/" + passType + ".hwpass");
        Files.write(signFilePath, randomPass);
    }

    /**
     * 从properties加载配置, 不推荐使用,请自己实现获取证书和私钥接口,这里测试的私钥未加密
     * 生产环境私钥一定要加密保存好, 建议签名服务与一般业务服务器分离部署!!!
     * 约束: Base字符串生成方法: cat ${filepath}|base64 -w 0 > ${filepath}.base64.txt
     * 私钥: passKeyPrefix + "certificate." + passTypeIdentifier=Base64(PKCS8私钥byte[])
     * 证书: passKeyPrefix + "certificate." + passTypeIdentifier=Base64(X.509证书文件byte[])
     * ca: passKeyPrefix + "ca"=Base64(CA证书byte[])
     *
     * @param properties the properties
     * @param passKeyPrefix the pass key prefix
     * @return the pass file service
     */
    public PassFileService getPassFileService(final Properties properties, final String passKeyPrefix) {
        PassCertificateService passCertificateService = new PassCertificateService() {
            @Override
            public byte[] getCertificateByPassType(String passTypeIdentifier) {
                String property = properties.getProperty(passKeyPrefix + "certificate." + passTypeIdentifier, "");
                return Base64.decodeBase64(property);
            }

            @Override
            public byte[] getPrivateKeyByPassType(String passTypeIdentifier) {
                String property = properties.getProperty(passKeyPrefix + "privatekey." + passTypeIdentifier, "");
                return Base64.decodeBase64(property);
            }

        };

        PassFileService passFileService = new PassFileServiceImpl(passCertificateService);
        return passFileService;
    }

    /**
     * Load properties.
     *
     * @param filePath the file path
     * @throws IOException the io exception
     */
    public void loadProperties(String filePath) throws IOException {
        byte[] bytes = loadTestBytes(filePath);
        Properties cert_properties = new Properties();
        cert_properties.load(new ByteArrayInputStream(bytes));
        Enumeration<?> enumeration = cert_properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement().toString();
            properties.setProperty(name, cert_properties.getProperty(name));
        }
    }

    /**
     * Load test bytes byte [ ].
     *
     * @param filePath the file path
     * @return the byte [ ]
     * @throws IOException the io exception
     */
    byte[] loadTestBytes(String filePath) throws IOException {
        InputStream resourceAsStream = PassFileServiceTestUtil.class.getResourceAsStream(filePath);
        return IOUtils.toByteArray(resourceAsStream);
    }

}
