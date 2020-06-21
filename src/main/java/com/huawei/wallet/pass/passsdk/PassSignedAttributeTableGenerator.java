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

import java.util.Hashtable;
import java.util.Map;

import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.cms.DefaultSignedAttributeTableGenerator;

public class PassSignedAttributeTableGenerator extends DefaultSignedAttributeTableGenerator {
    @Override
    protected Hashtable createStandardAttributeTable(Map parameters) {
        Hashtable ret = super.createStandardAttributeTable(parameters);
        ret.remove(CMSAttributes.cmsAlgorithmProtect);
        return ret;
    }
}
