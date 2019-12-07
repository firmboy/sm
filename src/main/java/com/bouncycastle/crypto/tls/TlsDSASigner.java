package com.bouncycastle.crypto.tls;

import java.security.SecureRandom;

import com.bouncycastle.crypto.CryptoException;
import com.bouncycastle.crypto.DSA;
import com.bouncycastle.crypto.Signer;
import com.bouncycastle.crypto.digests.NullDigest;
import com.bouncycastle.crypto.digests.SHA1Digest;
import com.bouncycastle.crypto.params.AsymmetricKeyParameter;
import com.bouncycastle.crypto.params.ParametersWithRandom;
import com.bouncycastle.crypto.signers.DSADigestSigner;

abstract class TlsDSASigner implements TlsSigner
{
    public byte[] calculateRawSignature(SecureRandom secureRandom, AsymmetricKeyParameter privateKey, byte[] md5andsha1)
        throws CryptoException
    {
        // Note: Only use the SHA1 part of the hash
        Signer signer = new DSADigestSigner(createDSAImpl(), new NullDigest());
        signer.init(true, new ParametersWithRandom(privateKey, secureRandom));
        signer.update(md5andsha1, 16, 20);
        return signer.generateSignature();
    }

    public Signer createVerifyer(AsymmetricKeyParameter publicKey)
    {
        Signer verifyer = new DSADigestSigner(createDSAImpl(), new SHA1Digest());
        verifyer.init(false, publicKey);
        return verifyer;
    }

    protected abstract DSA createDSAImpl();
}
