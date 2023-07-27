# Detached-Signature
In some case, we want to transfer files via SFTP, but sender/receiver need to ensure that the file transfers have not been modified between transferring. So we want us to signing the file to an xml format and attached to SFTP as well to verify that the signature is valid. (Not modified after signed)

## Requirements
- JDK installed in your computer atleaset version 1.8
- Bouncy castle version 1.69

## Classes highlight
- PromptBizApplication, example code of how we list the files then sign each files and write out to signature.xml
- FileService, class to list a file
- XmlDigitalSignature, class to handle sign and verify process
- KeyStoreUtils, class generate PrivateKey and X509Certificate
  
## Detach Signature format
The detach signature will stored in an xml format like this
- The xml having a <Reference> referring to original file name of this signature.
- The detach signature will write out in one line of xml string, below example was formatted to be ease of reading.

```xml
<Signature xmlns="http://www.w3.org/2000/09/xmldsig#">
  <SignedInfo>
        <CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n-20010315" />
        <SignatureMethod Algorithm="http://www.w3.org/2001/04/xmldsig-more#rsa-sha256" />
        <Reference URI="Decrypted_SON_ECN21202307000013.txt">
            <DigestMethod Algorithm="http://www.w3.org/2001/04/xmlenc#sha256" />
            <DigestValue>4mC7pAFWd6LyDKHOmzOdvlYUWRe3os5BNbo8Q/hc4nQ=</DigestValue>
        </Reference>
    </SignedInfo>
    <SignatureValue>
        RcOEndxAbCJRg6VFSzqi2s8HM30ufebbHOVcbVsbXvku7bGDFl4x0fZ3zPvpccgxWGZktmT7fzTiht8feEiPTKjXvqJojz+0tYrwj9b70SChOYnPPgIhLToHvJH5nofQjA5OcPShI/AlSq/82EfXJ0cJYXChFOC+BF0Fe5PyTq0WxAPqn2Wit+lf2uVpQiZr4iQ/6AjZW47CzPzm+D0stymbxISMCeocmPp00B0rXSUXsuJbG4KEXAU040CWo7sEdsSlhXrdf3lLCudNQjHnMi+x3OPn7DA69gGkU37XmvZ+ZqxvD5qjm20qx7S2CMyBAjGQfZH/ldjldEqcOrQ+ww==</SignatureValue>
    <KeyInfo>
        <X509Data>
            <X509SubjectName>CN=Self-Signed Certificate</X509SubjectName>
            <X509Certificate>
                MIICwjCCAaqgAwIBAgIGAYmTXgrLMA0GCSqGSIb3DQEBCwUAMCIxIDAeBgNVBAMMF1NlbGYtU2lnbmVkIENlcnRpZmljYXRlMB4XDTIzMDcyNjE4MDI0MVoXDTI0MDcyNTE4MDI0MVowIjEgMB4GA1UEAwwXU2VsZi1TaWduZWQgQ2VydGlmaWNhdGUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC54D6VisRtrKW5q8f4176SGX+wmb6Y85ME9wtcwtH8zbb9sWIoyxxwWLBbL4q2rHU9/eS+47zGBL2R4UK+2s6Gw4Lyt6CQxUoV21sWDc18ll0keFCEonjdJHn7ukJgNyNtiI6C5sRFF/jNdg8YoveHWlEbczp/brHSy4zRfyNsoI01RddvwIvrFXB+gFGdi9KQ7OecRfJHiTU3HhF3LdZt4mKL3AwH3H+IfAcMxdvm6Joup886afgqhPZ8S3mAalVNhxUXV5Va1Bw5YKg9Dzya9XZYWznS8WmSIfuBgRPsLKt7Mr9b2cKJ6LWJIUnHBvDdpWSpIIBVhWTPseBnFQIlAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAGsPoPw7MUoI2OLWqWuFhxR3G+AqtSPkhlq/6310A7xSLKgh7ihZhryd2wugBQcUUd8vWfed2h7OOau62ydtSiJwZaNbfEigkdeO1eR3nK42XwbbDaYx6FcU9DdDc026LfblGOxhd6SU5X9JtUQyBaDB0Qe6yD3aGKT5gVvvfBbsLjeM4s3Pt9mJCzmPwDNxuvrZIdkPHJ3JL9K1n91DL+z4ZBCbxWLPMKTHwfBOKeWQKBQIXOjRzJ2lDRlY3z6ScR+J7GwW/9DnT+smNjCnAiHjAz+loncS93OEzElXV6o+Ci32EVUMLiHf+O4zQT7Z8aBWluy1L/okXfhi8N3rh8I=</X509Certificate>
        </X509Data>
        <KeyValue>
            <RSAKeyValue>
                <Modulus>
                    ueA+lYrEbayluavH+Ne+khl/sJm+mPOTBPcLXMLR/M22/bFiKMsccFiwWy+Ktqx1Pf3kvuO8xgS9keFCvtrOhsOC8regkMVKFdtbFg3NfJZdJHhQhKJ43SR5+7pCYDcjbYiOgubERRf4zXYPGKL3h1pRG3M6f26x0suM0X8jbKCNNUXXb8CL6xVwfoBRnYvSkOznnEXyR4k1Nx4Rdy3WbeJii9wMB9x/iHwHDMXb5uiaLqfPOmn4KoT2fEt5gGpVTYcVF1eVWtQcOWCoPQ88mvV2WFs50vFpkiH7gYET7CyrezK/W9nCiei1iSFJxwbw3aVkqSCAVYVkz7HgZxUCJQ==</Modulus>
                <Exponent>AQAB</Exponent>
            </RSAKeyValue>
        </KeyValue>
    </KeyInfo>
</Signature>
