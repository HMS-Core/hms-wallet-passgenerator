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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Implementation of pass file service.
 *
 * @since 2019 -08-06
 */
public class PassFileServiceImpl implements PassFileService {

    public static final String PASS_SIGNATURE = "signature";

    public static final String MANIFEST_JSON = "manifest.json";

    public static final String PASS_JSON = "hwpass.json";

    public static final String PASS_PASSTYPEIDENTIFIER = "passTypeIdentifier";

    public static final String PASS_SERIALNUMBER = "serialNumber";

    public static final String PASS_WEBSERVICEURL = "webServiceURL";

    public static final String PASS_AUTHORIZATIONTOKEN = "authorizationToken";

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    PassUtilService passUtilService;

    PassSignService passSignService;

    PassSignService passSignVerifyService;

    PassCertificateService passCertificateService;

    public PassUtilService getPassUtilService() {
        return passUtilService;
    }

    public void setPassUtilService(PassUtilService passUtilService) {
        this.passUtilService = passUtilService;
    }

    public PassSignService getPassSignService() {
        return passSignService;
    }

    public void setPassSignService(PassSignService passSignService) {
        this.passSignService = passSignService;
    }

    public PassSignService getPassSignVerifyService() {
        return passSignVerifyService;
    }

    public void setPassSignVerifyService(PassSignService passSignVerifyService) {
        this.passSignVerifyService = passSignVerifyService;
    }

    public PassCertificateService getPassCertificateService() {
        return passCertificateService;
    }

    public void setPassCertificateService(PassCertificateService passCertificateService) {
        this.passCertificateService = passCertificateService;
    }

    /**
     * Instantiates a new Pass file service.
     *
     * @param passCertificateService the pass certificate service.
     */
    public PassFileServiceImpl(PassCertificateService passCertificateService) {
        this.passCertificateService = passCertificateService;
        this.passUtilService = new PassUtilServiceImpl();
        this.passSignService = new PassSignServiceBCImpl();
        this.passSignVerifyService = new PassSignServiceBCImpl();
    }

    private String parsePassTypeIdentifier(ObjectNode passJson) {
        String ret = null;
        JsonNode typeNode = passJson.path(PASS_PASSTYPEIDENTIFIER);
        if (!typeNode.isMissingNode() && !typeNode.isNull()) {
            ret = typeNode.asText();
        }
        return ret;
    }

    private byte[] createSignPass(Map<String, byte[]> fileMap, ObjectNode passJson, byte[] privateKey,
        byte[] passCertificate) throws PassException {
        byte[] unSignPass = createUnSignPass(fileMap, passJson);
        byte[] signPassBytes = signPass(unSignPass, privateKey, passCertificate);
        return signPassBytes;
    }

    private byte[] createPass(Map<String, byte[]> fileMap, ObjectNode passJson) throws PassException {
        String passTypeIdentifier = parsePassTypeIdentifier(passJson);
        if (passTypeIdentifier == null || passTypeIdentifier.isEmpty()) {
            throw new PassException("passTypeIdentifier is null or empty");
        }
        byte[] privateKey = passCertificateService.getPrivateKeyByPassType(passTypeIdentifier);
        byte[] certificateByPassType = passCertificateService.getCertificateByPassType(passTypeIdentifier);
        return createSignPass(fileMap, passJson, privateKey, certificateByPassType);
    }

    @Override
    public byte[] signMessage(byte[] message, String passTypeIdentifier) throws PassException {
        return passSignService.sign(message, passCertificateService.getPrivateKeyByPassType(passTypeIdentifier),
            passCertificateService.getCertificateByPassType(passTypeIdentifier));
    }

    private byte[] createUnSignPass(Map<String, byte[]> fileMap, ObjectNode passJson) throws PassException {
        ByteArrayOutputStream memoryZipArray = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(memoryZipArray);
        try {
            ZipEntry zipEntry = new ZipEntry(PASS_JSON);
            out.putNextEntry(zipEntry);
            byte[] contentByte = passJson.toString().getBytes(StandardCharsets.UTF_8);
            out.write(contentByte);
            out.closeEntry();

            Set<Entry<String, byte[]>> entries = fileMap.entrySet();
            for (Entry<String, byte[]> entry : entries) {
                String fileName = entry.getKey();
                ZipEntry ze = new ZipEntry(fileName);
                out.putNextEntry(ze);
                out.write(entry.getValue());
                out.closeEntry();
            }
            out.close();
        } catch (Exception e) {
            throw new PassException("createUnSignPass fail", e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(memoryZipArray);
        }
        return memoryZipArray.toByteArray();
    }

    private byte[] signPass(byte[] unSignPassZipBytes, byte[] privateKey, byte[] passCertificate) throws PassException {

        ByteArrayOutputStream memoryZipArray = new ByteArrayOutputStream();
        final ZipOutputStream zipOut = new ZipOutputStream(memoryZipArray);
        try {

            final ObjectNode manifestJson = JsonNodeFactory.instance.objectNode();
            ZipInputStreamHandler signHandler = new ZipInputStreamHandler() {

                @Override
                public boolean needSkip(ZipEntry entry) {
                    String entryName = entry.getName();
                    boolean needSkip = entryName.equals(PASS_SIGNATURE) || entryName.equals(MANIFEST_JSON);

                    if (entry.isDirectory() || needSkip) {
                        return true;
                    }
                    return false;
                }

                @Override
                public void handleContent(ZipEntry entry, byte[] unzipContent)
                    throws IOException, NoSuchAlgorithmException {
                    String entryName = entry.getName();
                    ZipEntry newEntry = new ZipEntry(entryName);
                    zipOut.putNextEntry(newEntry);
                    zipOut.write(unzipContent);
                    zipOut.closeEntry();
                    String fileHash = passUtilService.toHexString(passUtilService.SHA256Hash(unzipContent));
                    manifestJson.put(entryName, fileHash);
                }
            };
            passUtilService.handleZipInputStream(unSignPassZipBytes, signHandler);

            ZipEntry newEntry = new ZipEntry(MANIFEST_JSON);
            zipOut.putNextEntry(newEntry);
            byte[] maniBytes = manifestJson.toString().getBytes(StandardCharsets.UTF_8);
            zipOut.write(maniBytes);
            zipOut.closeEntry();

            byte[] signBytes = passSignService.sign(maniBytes, privateKey, passCertificate);
            ZipEntry signEntry = new ZipEntry(PASS_SIGNATURE);
            zipOut.putNextEntry(signEntry);
            zipOut.write(signBytes);
            zipOut.closeEntry();
            zipOut.close();
        } catch (Exception e) {
            throw new PassException("signPass fail", e);
        } finally {
            IOUtils.closeQuietly(zipOut);
            IOUtils.closeQuietly(memoryZipArray);
        }
        return memoryZipArray.toByteArray();
    }

    @Override
    public byte[] createPass(Map<String, byte[]> fileMap, String passJson) throws PassException {
        try {
            return createPass(fileMap, JSON_MAPPER.readValue(passJson, ObjectNode.class));
        } catch (Exception e) {
            throw new PassException("createPass fail", e);
        }
    }

}
