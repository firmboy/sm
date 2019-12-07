package com.bouncycastle.crypto.tls;

import java.security.SecureRandom;

import com.bouncycastle.crypto.CryptoException;
import com.bouncycastle.crypto.Signer;
import com.bouncycastle.crypto.params.AsymmetricKeyParameter;

interface TlsSigner
{
    byte[] calculateRawSignature(SecureRandom random, AsymmetricKeyParameter privateKey, byte[] md5andsha1)
        throws CryptoException;

    Signer createVerifyer(AsymmetricKeyParameter publicKey);

    boolean isValidPublicKey(AsymmetricKeyParameter publicKey);
}
