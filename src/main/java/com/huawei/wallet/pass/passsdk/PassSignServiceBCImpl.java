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

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

class PassSignServiceBCImpl implements PassSignService {

    public static final String PEM_BEGIN_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----";

    public static final String PEM_END_PRIVATE_KEY = "-----END PRIVATE KEY-----";

    String SIGN_ALGORITHMS_SHA256 = "SHA256WithRSA";

    PassUtilService utilService;

    /**
     * Instantiates a new Pass sign service bc.
     */
    public PassSignServiceBCImpl() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        this.utilService = new PassUtilServiceImpl();
    }

    @Override
    public byte[] sign(byte[] content, byte[] privateKeyBytes, byte[] passCertificate) throws PassException {
        ByteArrayInputStream passTypeCertificateStream = null;
        try {
            passTypeCertificateStream = new ByteArrayInputStream(passCertificate);
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate passTypeCertificate =
                (X509Certificate) certificateFactory.generateCertificate(passTypeCertificateStream);

            String key = new String(privateKeyBytes, StandardCharsets.UTF_8).replaceAll("\\n", "")
                .replace(PEM_BEGIN_PRIVATE_KEY, "")
                .replace(PEM_END_PRIVATE_KEY, "");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(key));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            CMSSignedDataGenerator cmsSignedDataGenerator = new CMSSignedDataGenerator();
            List<X509Certificate> certificateList = new ArrayList<>();
            certificateList.add(passTypeCertificate);
            Store certStore = new JcaCertStore(certificateList);
            cmsSignedDataGenerator.addCertificates(certStore);

            ContentSigner sha256Siger =
                new JcaContentSignerBuilder(SIGN_ALGORITHMS_SHA256).setProvider(BouncyCastleProvider.PROVIDER_NAME)
                    .build(privateKey);

            cmsSignedDataGenerator.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(
                new JcaDigestCalculatorProviderBuilder().setProvider(BouncyCastleProvider.PROVIDER_NAME).build())
                    .setSignedAttributeGenerator(new PassSignedAttributeTableGenerator())
                    .build(sha256Siger, passTypeCertificate));

            CMSTypedData chainMessage = new CMSProcessableByteArray(content);
            CMSSignedData signedData = cmsSignedDataGenerator.generate(chainMessage, false);

            ContentInfo asn1Structure = signedData.toASN1Structure();
            SignedData instance = SignedData.getInstance(asn1Structure.getContent());

            ASN1Encodable octs = new DEROctetString(content);
            ContentInfo encInfo = new ContentInfo(chainMessage.getContentType(), octs);

            SignedData newSignData = new SignedData(instance.getDigestAlgorithms(), encInfo, instance.getCertificates(),
                instance.getCRLs(), instance.getSignerInfos());

            ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.signedData, newSignData);

            CMSSignedData newCmsSignedData = new CMSSignedData(chainMessage, contentInfo);

            return newCmsSignedData.getEncoded();
        } catch (OperatorCreationException e) {
            throw new PassException("OperatorCreationException", e);
        } catch (CMSException e) {
            throw new PassException("CMSException", e);
        } catch (Exception e) {
            throw new PassException("sign fail", e);
        } finally {
            IOUtils.closeQuietly(passTypeCertificateStream);
        }
    }
}
