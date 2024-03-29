package nextstep.mvc.tobe.hadler;

import com.google.common.collect.Maps;
import nextstep.mvc.HandlerMapping;
import nextstep.mvc.tobe.util.ComponentScanner;
import nextstep.web.annotation.RequestMapping;
import nextstep.web.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class AnnotationHandlerMapping implements HandlerMapping {
    private Object[] basePackage;
    private ComponentScanner componentScanner;

    private Map<HandlerKey, HandlerExecution> handlerExecutions = Maps.newHashMap();

    public AnnotationHandlerMapping(Object... basePackage) {
        this.basePackage = basePackage;
        this.componentScanner = new ComponentScanner(basePackage);
    }

    @Override
    public void initialize() {
        Set<Class<?>> annotatedWithController = componentScanner.findController();

        for (Class<?> clazz : annotatedWithController) {
            Method[] methods = clazz.getDeclaredMethods();
            checkMethod(componentScanner.createInstance(clazz), methods);
        }
    }

    private void checkMethod(Object object, Method[] methods) {
        for (Method method : methods) {
            addHandlerExecution(object, method);
        }
    }

    private void addHandlerExecution(Object object, Method method) {
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            RequestMethod[] requestMethods = getRequestMethods(requestMapping);
            HandlerExecution handlerExecution = new HandlerExecution(object, method);

            Arrays.stream(requestMethods)
                    .map(value -> new HandlerKey(requestMapping.value(), value))
                    .forEach(key -> handlerExecutions.put(key, handlerExecution));
        }
    }

    private RequestMethod[] getRequestMethods(RequestMapping requestMapping) {
        RequestMethod[] requestMethods = requestMapping.method();

        if (requestMapping.method().length == 0) {
            requestMethods = RequestMethod.values();
        }

        return requestMethods;
    }

    @Override
    public HandlerExecution getHandler(HttpServletRequest request) {
        HandlerKey handlerKey = new HandlerKey(request.getRequestURI(), RequestMethod.valueOf(request.getMethod()));
        return handlerExecutions.get(handlerKey);
    }
}
