package com.linux.jobs.boundry

import com.linux.jobs.control.JobForm
import com.linux.jobs.entity.Job
import com.vaadin.flow.component.avatar.Avatar
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dependency.NpmPackage
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.PWA
import com.vaadin.flow.theme.Theme
import javax.websocket.Endpoint
import javax.websocket.server.ServerApplicationConfig
import javax.websocket.server.ServerEndpointConfig

@Theme
@PWA(name = "Community Referrals", shortName = "referrals", description = "A Simple Community referral app to replace the spreadsheet and having to join a what's app group.", offlineResources = [])
@NpmPackage(value = "line-awesome", version = "1.3.0")
class AppConfig : AppShellConfigurator, ServerApplicationConfig {
    override fun getEndpointConfigs(endpointClasses: MutableSet<Class<out Endpoint>>?) = mutableSetOf<ServerEndpointConfig>()
    override fun getAnnotatedEndpointClasses(scanned: MutableSet<Class<*>>?) = mutableSetOf<Class<*>>()
}

@Suppress("unused")
@Route(value = "")
@PageTitle("Jobs")
class JobsListView : VerticalLayout() {
    init {
        addClassName("jobs-list")
        setSizeFull()

        add(createToolbar(), layout())
    }

    private fun layout() = HorizontalLayout(
        createGrid(), JobForm().apply { width = "25em" }
    ).apply {
        addClassName("content")
        setFlexGrow(2.0, this.getComponentAt(0))
        setFlexGrow(1.0, this.getComponentAt(1))
        setSizeFull()
    }

    private fun createGrid() = Grid<Job>().apply {
        addClassName("job-grid")
        setSizeFull()
//        addColumn {Avatar(it.company) }.setHeader("Avatar")
        addColumn { it.company }.setHeader("Company")
        addColumn { it.currentJobOpenings }.setHeader("Information About The Opening")
        addColumn { it.domains.joinToString { "," } }.setHeader("Job Domains")
        addColumn { it.slots.value }.setHeader("Number Of Openings")
        addColumn {it.contact.name }.setHeader("Contact Information")
        columns.forEach { it.setAutoWidth(true) }
    }

    private fun createToolbar() = HorizontalLayout(TextField().apply {
        placeholder = "Filter by Company"
        isClearButtonVisible = true
        valueChangeMode = ValueChangeMode.LAZY
    }, Button("Add Job")).apply {
        addClassName("toolbar")
    }


}