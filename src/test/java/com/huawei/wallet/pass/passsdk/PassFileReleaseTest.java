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

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class PassFileReleaseTest {

    private PassFileServiceTestUtil passFileServiceTestUtil;

    private PassFileService passFileService;

    @Before
    public void setUp() throws IOException {
        passFileServiceTestUtil = new PassFileServiceTestUtil();
        passFileServiceTestUtil.loadProperties("/release.config.properties");
        passFileService = passFileServiceTestUtil.getPassFileService(passFileServiceTestUtil.getProperties(), "pass.");
    }

    /**
     * 开发者可以使用此方法获取新增/更新pass包的码流
     */
    @Test
    public void testCreateMessage() throws PassException {
        passFileServiceTestUtil.testCreateSignMessage(PassFileServiceTestUtil.TEST_PASS_TYPE, passFileService);
    }

    /**
     * 开发者可以使用此方法生成pass包
     */
    @Test
    public void testCreatePassFile() throws IOException, PassException {
        passFileServiceTestUtil.testCreatePassFile(PassFileServiceTestUtil.TEST_PASS_TYPE, passFileService);
    }
}
