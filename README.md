# HMS Wallet Passgenerator
## Table of Contents

 * [Introduction](#introduction)
 * [Installation](#installation)
 * [Configuration ](#configuration )
 * [Supported Environments](#supported-environments)
 * [Sample Code](#sample-code)
 * [License](#license)

## Introduction
This is sample code for how to create wallet pass package.

## Installation
Before running the Demo code, you should have installed Java and Maven. You should have gotten a .pem file and a .cer file.

## Supported Environments
Oracle Java 1.8 is recommended.

## Configuration 
1. Edit the "src\test\resources\release.config.properties" according to your service ID, pem file, and cer file.

1.1  Set the "pass.privatekey.YourServiceID" paramter.
For example, if your service ID is "hwpass.com.huawei.wallet.pass.sdktest", rename the paramter to "pass.privatekey.hwpass.com.huawei.wallet.pass.sdktest". Then do base64 encoding to the entire .pem file.
Then set the base64 string as the value of this paramter.
        
1.2 Set the "pass.certificate.YourServiceID" paramter.
For example, if your service ID is "hwpass.com.huawei.wallet.pass.sdktest", rename the paramter to "pass.certificate.hwpass.com.huawei.wallet.pass.sdktest". Then do base64 encoding to the entire .cer file.
Then set the base64 string as the value of this paramter.

2. Set your service ID to the "TEST_PASS_TYPE" parameter in the "src\test\java\PassFileServiceTestUtil.java" file.

## Sample Code
1. Run the "testCreatePassFile" method in the "src\test\java\PassFileReleaseTest.java" file to create a .hwpass file.
    
2. Run the "testCreateMessage" method in the "src\test\java\PassFileReleaseTest.java" file to create a signature.

## License
HMS wallet server sample code is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
