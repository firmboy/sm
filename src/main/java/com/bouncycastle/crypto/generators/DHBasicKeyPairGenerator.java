package com.bouncycastle.crypto.generators;

import com.bouncycastle.crypto.AsymmetricCipherKeyPair;
import com.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import com.bouncycastle.crypto.KeyGenerationParameters;
import com.bouncycastle.crypto.params.DHKeyGenerationParameters;
import com.bouncycastle.crypto.params.DHParameters;
import com.bouncycastle.crypto.params.DHPrivateKeyParameters;
import com.bouncycastle.crypto.params.DHPublicKeyParameters;

import java.math.BigInteger;

/**
 * a basic Diffie-Hellman key pair generator.
 *
 * This generates keys consistent for use with the basic algorithm for
 * Diffie-Hellman.
 */
public class DHBasicKeyPairGenerator
    implements AsymmetricCipherKeyPairGenerator
{
    private DHKeyGenerationParameters param;

    public void init(
        KeyGenerationParameters param)
    {
        this.param = (DHKeyGenerationParameters)param;
    }

    public AsymmetricCipherKeyPair generateKeyPair()
    {
        DHKeyGeneratorHelper helper = DHKeyGeneratorHelper.INSTANCE;
        DHParameters dhp = param.getParameters();

        BigInteger x = helper.calculatePrivate(dhp, param.getRandom()); 
        BigInteger y = helper.calculatePublic(dhp, x);

        return new AsymmetricCipherKeyPair(
            new DHPublicKeyParameters(y, dhp),
            new DHPrivateKeyParameters(x, dhp));
    }
}
