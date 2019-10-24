package net.fender.pogo;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

public class IgnoreNullBeanUtilsBean extends BeanUtilsBean {

    @Override
    public void copyProperty(Object dest, String name, Object value)
            throws IllegalAccessException, InvocationTargetException {
        if (value == null) return;
        if (value instanceof Collection) {
            Collection collection = (Collection) value;
            if (collection.isEmpty()) return;
        }
        super.copyProperty(dest, name, value);
    }
}
