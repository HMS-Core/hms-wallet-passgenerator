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

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Interface of pass utility service.
 *
 * @since 2019-08-06
 */
public interface PassUtilService {
    /**
     * Convert a long parameter to bytes.
     *
     * @param x the parameter to be converted.
     * @return the converted bytes.
     */
    byte[] longToBytes(long x);

    /**
     * Get a compressed UUID. A timestamp is added to the UUID to keep UUID in order.
     *
     * @return the compressed UUID.
     */
    String getCompressUUID();

    /**
     * Convert hex bytes to a string.
     *
     * @param bytes the hex bytes.
     * @return the converted string.
     */
    String toHexString(byte[] bytes);

    /**
     * Handle zip input stream.
     *
     * @param passBytes the pass bytes.
     * @param handler the handler.
     * @throws IOException the IO exception.
     */
    void handleZipInputStream(byte[] passBytes, ZipInputStreamHandler handler) throws IOException;

    /**
     * Encrypt data with SHA256 algorithm.
     *
     * @param content the content to be encrypted.
     * @return the encrypted bytes.
     * @throws NoSuchAlgorithmException the no-such-algorithm exception.
     */
    byte[] SHA256Hash(byte[] content) throws NoSuchAlgorithmException;
}
