package com.bouncycastle.crypto.tls;

import com.bouncycastle.crypto.DSA;
import com.bouncycastle.crypto.params.AsymmetricKeyParameter;
import com.bouncycastle.crypto.params.DSAPublicKeyParameters;
import com.bouncycastle.crypto.signers.DSASigner;

class TlsDSSSigner extends TlsDSASigner
{
    public boolean isValidPublicKey(AsymmetricKeyParameter publicKey)
    {
        return publicKey instanceof DSAPublicKeyParameters;
    }

    protected DSA createDSAImpl()
    {
        return new DSASigner();
    }
}
