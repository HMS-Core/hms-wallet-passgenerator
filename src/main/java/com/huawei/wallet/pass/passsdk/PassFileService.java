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

import java.util.Map;

/**
 * Interface of pass file service.
 *
 * @since 2019-08-06
 */
public interface PassFileService {

    /**
     * Create pass data.
     *
     * @param fileMap the file map
     * @param passJson the pass json
     * @return byte [ ]
     * @throws PassException the pass exception
     */
    byte[] createPass(Map<String, byte[]> fileMap, String passJson) throws PassException;

    /**
     * Sign a message with the certificate according to passType.
     *
     * @param message the message to be signed
     * @param passTypeIdentifier the pass type identifier
     * @return the signed bytes.
     * @throws PassException the pass exception
     */
    byte[] signMessage(byte[] message, String passTypeIdentifier) throws PassException;
}
