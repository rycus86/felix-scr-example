package me.vadam.example.felix.scr.module.one.impl;

import me.vadam.example.felix.scr.module.one.FirstComponent;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true)
public class FirstComponentImpl implements FirstComponent {

    @Activate
    public void activate(ComponentContext context) {
        System.out.println("I'm here...");
    }

    @Override
    public String sayHello() {
        return "Hello";
    }
}
