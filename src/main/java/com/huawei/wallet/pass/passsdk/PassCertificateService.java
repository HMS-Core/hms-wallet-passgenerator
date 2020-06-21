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

/**
 * Interface of pass certificate service.
 *
 * @since 2019-08-06
 */
public interface PassCertificateService {
    /**
     * Get a X.509 certificate by passTypeIdentifier.
     *
     * @param passTypeIdentifier the passTypeIdentifier.
     * @return the certificate bytes.
     */
    byte[] getCertificateByPassType(String passTypeIdentifier);

    /**
     * Get a PKCS8 private key by passTypeIdentifier.
     *
     * @param passTypeIdentifier the passTypeIdentifier.
     * @return the private key bytes.
     */
    byte[] getPrivateKeyByPassType(String passTypeIdentifier);
}
