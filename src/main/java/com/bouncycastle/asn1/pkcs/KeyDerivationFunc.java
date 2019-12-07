package com.bouncycastle.asn1.pkcs;

import com.bouncycastle.asn1.ASN1Encodable;
import com.bouncycastle.asn1.ASN1Sequence;
import com.bouncycastle.asn1.DERObjectIdentifier;
import com.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class KeyDerivationFunc
    extends AlgorithmIdentifier
{
    KeyDerivationFunc(
        ASN1Sequence seq)
    {
        super(seq);
    }
    
    public KeyDerivationFunc(
        DERObjectIdentifier id,
        ASN1Encodable params)
    {
        super(id, params);
    }
}
