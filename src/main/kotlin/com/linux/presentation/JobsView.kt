package com.linux.presentation

import com.linux.contact.control.ContactDataControl
import com.linux.contact.entity.Contact
import com.linux.jobs.control.JobDataControl
import com.linux.jobs.entity.Job
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.VaadinServletRequest
import io.quarkus.narayana.jta.QuarkusTransaction
import io.quarkus.oidc.IdToken
import io.quarkus.security.identity.SecurityIdentity
import org.eclipse.microprofile.jwt.JsonWebToken
import org.jboss.logging.Logger
import javax.annotation.PostConstruct
import javax.inject.Inject

@Suppress("unused")
@Route(value = "")
@PageTitle("Jobs")
class JobsView(
    @Inject @IdToken var idToken: JsonWebToken,
    @Inject var securityIdentity: SecurityIdentity,
    @Inject var jobDataControl: JobDataControl,
    @Inject var contactDataControl: ContactDataControl
) : VerticalLayout() {
    private val l = Logger.getLogger(JobsView::class.java)
    private final val grid = createGrid()
    private final val jobForm = JobForm()
    private final val companyNameFilter = TextField()

    @PostConstruct
    fun postConstruct() {
        l.infov("------idToken name : $idToken ------")
        l.infov("------ securityIdentity.roles : ${securityIdentity.roles} ------")
        l.info("------ VaadinServletRequest.getCurrent().userPrincipal : ${VaadinServletRequest.getCurrent().userPrincipal} ------")

        if (contactDataControl.contactAbsent(idToken.name)) {
            QuarkusTransaction.run { contactDataControl.persist(Contact(email = idToken.name, givenName = idToken.getClaim("givenName"), familyName = idToken.getClaim("familyName"))) }
        }

        companyNameFilter.apply {
            placeholder = "Filter by Company"
            isClearButtonVisible = true
            valueChangeMode = ValueChangeMode.LAZY
        }
//
//        if (securityIdentity.isAnonymous) {
//
//        }


        with(jobForm) {
            width = "25em"

            addListener(SaveEvent::class.java) {
                jobDataControl.persist(it.job!!)
                updateList()
                closeEditor()
            }
            addListener(DeleteEvent::class.java) { println(it) }
            addListener(CloseEvent::class.java) { closeEditor() }
        }

        grid.asSingleSelect().addValueChangeListener { event -> onRowSelected(event.value, jobForm) }

        add(HorizontalLayout(companyNameFilter, Button("Add Job") {
            grid.asSingleSelect().clear()
            onRowSelected(Job(), jobForm)
        }, Span(idToken.name).apply { element.themeList.add("badge success")} ).apply { addClassName("toolbar") }, HorizontalLayout(grid, jobForm).apply {
            addClassName("content")
            setFlexGrow(2.0, getComponentAt(0))
            setFlexGrow(1.0, getComponentAt(1))
            setSizeFull()
        })
        addClassName("jobs-list")
        setSizeFull()
    }

    private fun checkOrCreateContact() {
//        if (contactDataControl.contactAbsent(idToken.name)) {
//            QuarkusTransaction.run { contactDataControl.persist(Contact(email = idToken.name, givenName = idToken.getClaim("givenName"), familyName = idToken.getClaim("familyName"))) }
//        }
    }


    private fun closeEditor() {
        jobForm.job = null
        jobForm.isVisible = false
        jobForm.removeClassName("editing")
    }

    private fun updateList() {
        grid.setItems(jobDataControl.list("company", companyNameFilter.value))
    }

    private fun createGrid() = Grid<Job>().apply {
        addClassName("job-grid")
        setSizeFull()
//        addColumn {Avatar(it.company) }.setHeader("Avatar")
        addColumn { it.company }.setHeader("Company").apply { isSortable = true; isFrozen = true }
        addColumn { it.currentJobOpenings }.setHeader("Information About The Opening").apply { isSortable = true; isFrozen = true }
        addColumn { it.domains.joinToString { "," } }.setHeader("Job Domains").apply { isSortable = true; isFrozen = true }
        addColumn { it.slots.value }.setHeader("Number Of Openings").apply { isSortable = true; isFrozen = true }
        addColumn { it.contact.name() }.setHeader("Contact Information").apply { isFrozen = true }
        columns.forEach { it.setAutoWidth(true) }
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
}