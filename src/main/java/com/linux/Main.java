package com.linux;

import com.linux.jobs.entity.Job;
import com.linux.presentation.*;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.internal.JavaScriptBootstrapUI;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.websocket.Endpoint;
import javax.websocket.server.*;
import java.util.*;

@Theme(themeClass = Lumo.class, variant = Lumo.DARK)
@PWA(name = "Community Referrals", shortName = "referrals", description = "A Simple Community referral app to replace the spreadsheet and having to join a what's app group.", offlineResources = {})
@NpmPackage(value = "line-awesome", version = "1.3.0")
@RegisterForReflection(targets = {Main.class, ClickEvent.class, PollEvent.class, KeyDownEvent.class, JavaScriptBootstrapUI.class, ComponentEvent.class, Job.class, JobForm.class, JobsView.class, MainView.class})
public class Main implements AppShellConfigurator, ServerApplicationConfig {
    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        return Set.of();
    }

    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        return Set.of();
    }
}