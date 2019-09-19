package me.vadam.example.felix.scr.module.two.impl;

import me.vadam.example.felix.scr.module.one.FirstComponent;
import me.vadam.example.felix.scr.module.two.SecondComponent;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component
public class SecondComponentImpl implements SecondComponent {

    private final FirstComponent firstComponent;

    @Activate
    public SecondComponentImpl(@Reference FirstComponent firstComponent) {
        this.firstComponent = firstComponent;
    }

    @Activate
    void anotherActivate() {
        System.out.println("Activation");
    }

    @Override
    public String transitiveHello() {
        return firstComponent.sayHello();
    }

    @Override
    public String append(String suffix) {
        return firstComponent.sayHello() + suffix;
    }
}
