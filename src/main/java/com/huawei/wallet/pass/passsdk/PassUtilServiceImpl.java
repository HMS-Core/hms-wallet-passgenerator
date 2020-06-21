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

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.codec.binary.Base64;

/**
 * Implementation of pass utility service.
 *
 * @since 2019-08-06
 */
public class PassUtilServiceImpl implements PassUtilService {

    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    String HASH_ALGORITHMS = "SHA-256";

    int TOOBIG = 16384000;

    int TOOMANY = 500;

    Integer BUFFER = 4096;

    @Override
    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / Byte.SIZE);
        buffer.putLong(x);
        return buffer.array();
    }

    @Override
    public String getCompressUUID() {
        SecureRandom rs = new SecureRandom();
        byte[] srbytes = new byte[24];
        rs.nextBytes(srbytes);
        byte[] byUuid = new byte[32];
        System.arraycopy(longToBytes(System.currentTimeMillis()), 0, byUuid, 0, 8);
        System.arraycopy(srbytes, 0, byUuid, 8, 24);
        return Base64.encodeBase64URLSafeString(byUuid);
    }

    @Override
    public String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            sb.append(HEX_DIGITS[(b >> 4) & 0xf]).append(HEX_DIGITS[b & 0xf]);
        }
        return sb.toString();
    }

    @Override
    public final void handleZipInputStream(byte[] passBytes, ZipInputStreamHandler handler) throws IOException {
        ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(passBytes));
        ZipEntry entry;
        int entries = 0;
        int total = 0;
        byte[] data = new byte[BUFFER];
        try {
            while ((entry = zis.getNextEntry()) != null) {
                BufferedOutputStream dest = null;
                int count;

                if (handler.needSkip(entry)) {
                    entries++;
                    // If the total number of entries is larger than the maximum entries number, throw exception.
                    if (entries > TOOMANY) {
                        throw new IllegalStateException("Too many files to unzip.");
                    }
                    continue;
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                dest = new BufferedOutputStream(byteArrayOutputStream, BUFFER);
                // Check every entry's size.
                while (total + BUFFER <= TOOBIG && (count = zis.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                    total += count;
                }
                dest.close();
                handler.handleContent(entry, byteArrayOutputStream.toByteArray());

                entries++;
                // If the total number of entries is larger than the limit, throw exception.
                if (entries > TOOMANY) {
                    throw new IllegalStateException("Too many files to unzip.");
                }
                // If the total size of zip files is bigger than the maximum size, throw exception.
                if (total + BUFFER > TOOBIG) {
                    throw new IllegalStateException("File being unzipped is too big.");
                }
                try {
                    if (dest != null) {
                        dest.flush();
                        dest.close();
                    }
                } catch (IOException e) {
                    throw e;
                }
                try {
                    if (zis != null) {
                        zis.closeEntry();
                    }
                } catch (IOException e) {
                    throw e;
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Handle zip File Fail", e);
        } finally {
            if (zis != null) {
                zis.close();
            }
        }
    }

    @Override
    public byte[] SHA256Hash(byte[] content) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHMS);
        messageDigest.update(content);
        return messageDigest.digest();
    }

}
