package net.fender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;

/**
 * * Created by eric.fenderbosch on 3/8/17.
 */
public class EnvironmentUtil {

    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentUtil.class);

    public static void addEnvironmentVariable(String key, String value) {
        getModifiableEnv().put(key, value);
    }

    public static void removeEnvironmentVariable(String key) {
        getModifiableEnv().remove(key);
    }

    public static void addEnvironmentVariables(Map<String, String> variables) {
        getModifiableEnv().putAll(variables);
    }

    private static Map<String, String> getModifiableEnv() {
        Class[] classes = Collections.class.getDeclaredClasses();
        Map<String, String> env = System.getenv();
        try {
            for (Class cl : classes) {
                if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                    Field field = cl.getDeclaredField("m");
                    // not a lambda so we can use annotations
                    AccessController.doPrivileged(new PrivilegedAction<Void>() {
                        @Override
                        public Void run() {
                            field.setAccessible(true);
                            return null;
                        }
                    });
                    Object obj = field.get(env);
                    return (Map<String, String>) obj;
                }
            }
        } catch (ReflectiveOperationException e) {
            LOG.error("error setting env vars", e);
        }
        return Collections.emptyMap();
    }
}
