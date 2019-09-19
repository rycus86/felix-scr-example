package me.vadam.example.felix.scr;

import me.vadam.example.felix.scr.module.one.FirstComponent;
import me.vadam.example.felix.scr.module.two.SecondComponent;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.main.AutoProcessor;
import org.osgi.framework.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Main {

    public static void main(String[] args) throws BundleException, InterruptedException {
        System.setProperty("org.osgi.service.log.admin.loglevel", "DEBUG");

        final Map<String, String> configMap = new HashMap<>();
        configMap.put(FelixConstants.LOG_LEVEL_PROP, Integer.toString(Logger.LOG_DEBUG));
        configMap.put("ds.loglevel", Integer.toString(Logger.LOG_DEBUG));
        configMap.put(AutoProcessor.AUTO_DEPLOY_ACTION_PROPERTY, "install,start");

        final Felix felix = new Felix(configMap);
        felix.init();

        AutoProcessor.process(configMap, felix.getBundleContext());

        felix.getBundleContext().addFrameworkListener(new FrameworkListener() {
            @Override
            public void frameworkEvent(FrameworkEvent event) {
                if (event.getType() == FrameworkEvent.STARTED) {
                    withInstanceOf(felix.getBundleContext(), FirstComponent.class, component -> {
                        System.out.println("Hello: " + component.sayHello());
                    });

                    withInstanceOf(felix.getBundleContext(), SecondComponent.class, component -> {
                        System.out.println("Transitive: " + component.transitiveHello());
                        System.out.println("Append: " + component.append(" world"));
                    });
                }
            }
        });

        felix.start();

        felix.waitForStop(0);
        System.exit(0);
    }

    static <T> void withInstanceOf(BundleContext context, Class<T> clazz, Consumer<T> consumer) {
        try {
            for (ServiceReference<?> ref : context.getAllServiceReferences(clazz.getName(), null)) {
                T proxy = createProxy(context.getService(ref), clazz);
                consumer.accept(proxy);
                context.ungetService(ref);
            }
        } catch (InvalidSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    static <T> T createProxy(Object instance, Class<T> clazz) {
        return (T) Proxy.newProxyInstance(Main.class.getClassLoader(), new Class<?>[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Method proxyMethod = instance.getClass().getMethod(method.getName(), method.getParameterTypes());
                return proxyMethod.invoke(instance, args);
            }
        });
    }

}
