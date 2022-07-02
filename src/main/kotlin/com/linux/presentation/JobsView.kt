package com.linux.presentation

import com.linux.jobs.entity.Job
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.VaadinServletRequest
import io.quarkus.oidc.IdToken
import io.quarkus.security.identity.SecurityIdentity
import org.eclipse.microprofile.jwt.JsonWebToken
import org.jboss.logging.Logger
import javax.inject.Inject

@Suppress("unused")
@Route(value = "")
@PageTitle("Jobs")
class JobsView(@Inject @IdToken var idToken: JsonWebToken, @Inject var securityIdentity: SecurityIdentity) : VerticalLayout() {
    private val l = Logger.getLogger(JobsView::class.java)

    init {
        l.infov("------idToken name : ${idToken.name} ------")
        l.infov("------ securityIdentity.roles : ${securityIdentity.roles} ------")
        l.info("------ VaadinServletRequest.getCurrent().userPrincipal : ${VaadinServletRequest.getCurrent().userPrincipal} ------")


        addClassName("jobs-list")
        setSizeFull()

        if (!securityIdentity.isAnonymous) {
            val jobForm = JobForm().apply { width = "25em" }
            val grid = createGrid(jobForm)

            add(createToolbar(grid, jobForm), HorizontalLayout(grid, jobForm).apply {
                addClassName("content")
                setFlexGrow(2.0, getComponentAt(0))
                setFlexGrow(1.0, getComponentAt(1))
                setSizeFull()
            })
        }
    }

    private fun createGrid(jobForm: JobForm) = Grid<Job>().apply {
        addClassName("job-grid")
        setSizeFull()
//        addColumn {Avatar(it.company) }.setHeader("Avatar")
        addColumn { it.company }.setHeader("Company").apply { isSortable = true; isFrozen = true }
        addColumn { it.currentJobOpenings }.setHeader("Information About The Opening").apply { isSortable = true; isFrozen = true }
        addColumn { it.domains.joinToString { "," } }.setHeader("Job Domains").apply { isSortable = true; isFrozen = true }
        addColumn { it.slots.value }.setHeader("Number Of Openings").apply { isSortable = true; isFrozen = true }
        addColumn { it.contact.name }.setHeader("Contact Information").apply { isFrozen = true }
        columns.forEach { it.setAutoWidth(true) }
        asSingleSelect().addValueChangeListener { event -> onRowSelected(event.value, jobForm) }
    }

    private fun onRowSelected(valueFromEvent: Job?, jobForm: JobForm) {
        if (valueFromEvent != null) {
            jobForm.addClassName("editing")
            jobForm.isVisible = true
            jobForm.job = valueFromEvent
            return
        }

        jobForm.removeClassName("editing")
        jobForm.isVisible = false
        jobForm.job = null
    }

    private fun createToolbar(grid: Grid<Job>, jobForm: JobForm) = HorizontalLayout(TextField().apply {
        placeholder = "Filter by Company"
        isClearButtonVisible = true
        valueChangeMode = ValueChangeMode.LAZY
    }, Button("Add Job") {
        grid.asSingleSelect().clear()
        onRowSelected(Job(), jobForm)
    }).apply {
        addClassName("toolbar")
    }
}