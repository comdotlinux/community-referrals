package com.linux

import com.linux.jobs.entity.Job
import com.linux.presentation.JobForm
import com.linux.presentation.JobsView
import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.KeyDownEvent
import com.vaadin.flow.component.PollEvent
import com.vaadin.flow.component.dependency.NpmPackage
import com.vaadin.flow.component.internal.JavaScriptBootstrapUI
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.server.PWA
import com.vaadin.flow.theme.Theme
import io.quarkus.runtime.annotations.RegisterForReflection
import javax.websocket.Endpoint
import javax.websocket.server.ServerApplicationConfig
import javax.websocket.server.ServerEndpointConfig

/**
 * Vaadin uses reflection behind the scenes, so we need to give some hints
 * for the native compilation. In optimal situation, Vaadin extension would
 * provide this list, but we are still not ready to do that for all of our
 * modules. You can collect the list manually while using the app, or get
 * that generated using a Java agent.
 *
 *
 * See e.g. [these great instructions](https://simply-how.com/fix-graalvm-native-image-compilation-issues#section-4).
 *
 *
 *
 * If you are not using the reflect-config.json directly, like in this example,
 * some of the hints generated by the agent are "false positives" and can be
 * ignored. Based on experience, all event classes needs to be on the list and
 *
 *
 * <pre>
 * {
 * "name":"com.example.starter.base.MainView",
 * "queryAllDeclaredMethods":true
 * },
</pre> *
 */
@RegisterForReflection(targets = [AppConfig::class, ClickEvent::class, PollEvent::class, KeyDownEvent::class, JavaScriptBootstrapUI::class, ComponentEvent::class, Job::class, JobForm::class, JobsView::class])
class ReflectionConfig


@Theme
@PWA(name = "Community Referrals", shortName = "referrals", description = "A Simple Community referral app to replace the spreadsheet and having to join a what's app group.", offlineResources = [])
@NpmPackage(value = "line-awesome", version = "1.3.0")
class AppConfig : AppShellConfigurator, ServerApplicationConfig {
    override fun getEndpointConfigs(endpointClasses: MutableSet<Class<out Endpoint>>?) = mutableSetOf<ServerEndpointConfig>()
    override fun getAnnotatedEndpointClasses(scanned: MutableSet<Class<*>>?) = mutableSetOf<Class<*>>()
}