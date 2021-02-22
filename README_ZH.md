# 华为钱包服务卡券包生成示例代码
## 目录

 * [简介](#简介)
 * [安装](#安装)
 * [配置](#配置)
 * [环境要求](#环境要求)
 * [示例代码](#示例代码)
 * [授权许可](#授权许可)

## 简介
该示例代码介绍了如何创建华为钱包服务的卡券包。

## 安装
在运行示例代码之前，确保已经安装了Java和Maven，并已经获得了.pem文件和.cer文件。

## 环境要求
推荐使用Oracle Java 1.8。

## 配置
1. 根据你的业务ID、pem文件和cer文件编辑 `src\test\resources\release.config.properties` 文件。

-  设置 `pass.privatekey.YourServiceID` 参数。
例如，业务ID为 `hwpass.com.huawei.wallet.pass.sdktest`, 则将该参数重命名为 `pass.privatekey.hwpass.com.huawei.wallet.pass.sdktest`. 对整个. pem文件进行base64编码，然后将该参数设置为编码后的字符串。
        
-  设置 `pass.certificate.YourServiceID` 参数。
例如，业务ID为 `hwpass.com.huawei.wallet.pass.sdktest`, 则将该参数重命名为 `pass.certificate.hwpass.com.huawei.wallet.pass.sdktest`. 对整个. cer文件进行base64编码，然后将该参数设置为编码后的字符串。

2. 设置 "src\test\java\PassFileServiceTestUtil.java" 文件中的 `TEST_PASS_TYPE` 参数为业务ID。

## 示例代码
1. 运行"src\test\java\PassFileReleaseTest.java"文件中的`testCreatePassFile` 方法，创建hwpass文件。
    
2. 运行"src\test\java\PassFileReleaseTest.java"文件中的`testCreateMessage` 方法，生成签名文件。

## 技术支持
如果您对HMS Core还处于评估阶段，可在[Reddit社区](https://www.reddit.com/r/HuaweiDevelopers/)获取关于HMS Core的最新讯息，并与其他开发者交流见解。

如果您对使用HMS示例代码有疑问，请尝试：
- 开发过程遇到问题上[Stack Overflow](https://stackoverflow.com/questions/tagged/huawei-mobile-services)，在`huawei-mobile-services`标签下提问，有华为研发专家在线一对一解决您的问题。
- 到[华为开发者论坛](https://developer.huawei.com/consumer/cn/forum/blockdisplay?fid=18) HMS Core板块与其他开发者进行交流。

如果您在尝试示例代码中遇到问题，请向仓库提交[issue](https://github.com/HMS-Core/hms-wallet-passgenerator/issues)，也欢迎您提交[Pull Request](https://github.com/HMS-Core/hms-wallet-passgenerator/pulls)。

## 授权许可
华为钱包服务卡券包生成示例代码通过[Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0)授权许可.
