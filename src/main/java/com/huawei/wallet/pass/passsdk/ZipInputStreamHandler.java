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

import java.util.zip.ZipEntry;

/**
 * Interface of zip-input-stream handler.
 *
 * @since 2019-08-06
 */
public interface ZipInputStreamHandler {
    /**
     * Check if a zip entry should be skipped.
     *
     * @param entry the entry.
     * @return if the entry should be skipped.
     */
    boolean needSkip(ZipEntry entry);

    /**
     * Handle a zip entry.
     *
     * @param entry the entry.
     * @param unzipContent unzipped content.
     * @throws Exception the exception.
     */
    void handleContent(ZipEntry entry, byte[] unzipContent) throws Exception;
}
