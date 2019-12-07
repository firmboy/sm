package com.bouncycastle.crypto.tls;

import com.bouncycastle.crypto.DSA;
import com.bouncycastle.crypto.params.AsymmetricKeyParameter;
import com.bouncycastle.crypto.params.ECPublicKeyParameters;
import com.bouncycastle.crypto.signers.ECDSASigner;

class TlsECDSASigner extends TlsDSASigner
{
    public boolean isValidPublicKey(AsymmetricKeyParameter publicKey)
    {
        return publicKey instanceof ECPublicKeyParameters;
    }

    protected DSA createDSAImpl()
    {
        return new ECDSASigner();
    }
}
