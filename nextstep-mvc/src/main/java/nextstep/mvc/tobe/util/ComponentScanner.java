package nextstep.mvc.tobe.util;

import nextstep.web.annotation.Controller;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.Set;

public class ComponentScanner {
    private Reflections reflections;

    public ComponentScanner(Object[] basePackage) {
        reflections = new Reflections(basePackage);
    }

    public Set<Class<?>> findController() {
        return reflections.getTypesAnnotatedWith(Controller.class);
    }

    public Object createInstance(Class<?> clazz) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("class has not no-arg constructor");
        }
    }
}
