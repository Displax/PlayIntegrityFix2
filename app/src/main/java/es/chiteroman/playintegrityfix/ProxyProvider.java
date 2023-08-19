package es.chiteroman.playintegrityfix;

import java.security.Provider;

public class ProxyProvider extends Provider {
    public ProxyProvider(Provider provider) {
        super(provider.getName(), provider.getVersion(), provider.getInfo());
        putAll(provider);
        put("KeyStore.AndroidKeyStore", ProxyKeyStoreSpi.class.getName());
    }

    @Override
    public synchronized Service getService(String type, String algorithm) {
        EntryPoint.spoofDevice();
        return super.getService(type, algorithm);
    }
}
