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
 * PassSDK exception class.
 *
 * @since 2019-08-06
 */
public class PassException extends Exception {
    /**
     * Instantiates a new pass exception.
     *
     * @param message exception message.
     */
    public PassException(String message) {
        super(message);
    }

    /**
     * Instantiates a new pass exception.
     *
     * @param message exception message.
     * @param cause exception cause.
     */
    public PassException(String message, Throwable cause) {
        super(message, cause);
    }
}
