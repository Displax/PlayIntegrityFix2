package es.chiteroman.playintegrityfix;

import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.Provider;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

public class EntryPoint {
    private static final String TAG = "SNFix/Java";
    private static final Map<String, String> map = new HashMap<>();

    static {
        map.put("MODEL", "Pixel XL");
        map.put("PRODUCT", "marlin");
        map.put("DEVICE", "marlin");
        map.put("FINGERPRINT", "google/marlin/marlin:7.1.2/NJH47F/4146041:user/release-keys");
    }

    public static void init() {
        try {
            spoofDevice();
            proxyProv();
        } catch (KeyStoreException | NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, e.toString());
        }
    }

    public static void spoofDevice() {
        map.forEach(EntryPoint::setBuildProps);
        Log.i(TAG, "Spoof device props");
    }

    private static void proxyProv() throws KeyStoreException, NoSuchFieldException, IllegalAccessException {
        Provider provider = Security.getProvider("AndroidKeyStore");

        Provider customProv = new ProxyProvider(provider);

        Security.removeProvider("AndroidKeyStore");
        Security.insertProviderAt(customProv, 1);

        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        Field keyStoreSpiField = keyStore.getClass().getDeclaredField("keyStoreSpi");

        keyStoreSpiField.setAccessible(true);
        ProxyKeyStoreSpi.keyStoreSpi = (KeyStoreSpi) keyStoreSpiField.get(keyStore);
        keyStoreSpiField.setAccessible(false);

        Log.i(TAG, "Set fake Provider");
    }

    private static void setBuildProps(String fieldName, String value) {
        try {
            Field field = Build.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(TAG, e.toString());
        }
    }
}
