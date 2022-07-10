package com.linux.presentation

import com.linux.auth.control.AuthenticationControl
import com.linux.contact.control.ContactDataControl
import com.linux.jobs.control.JobDataControl
import com.linux.jobs.entity.Job
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import io.quarkus.narayana.jta.QuarkusTransaction
import io.quarkus.oidc.IdToken
import org.eclipse.microprofile.jwt.JsonWebToken
import org.jboss.logging.Logger
import javax.annotation.PostConstruct
import javax.inject.Inject

@Suppress("unused")
@Route(value = "", layout = MainView::class)
@PageTitle("Jobs")
class JobsView(
    @Inject @IdToken var idToken: JsonWebToken,
    @Inject var authControl: AuthenticationControl,
    @Inject var jobDataControl: JobDataControl,
    @Inject var contactDataControl: ContactDataControl
) : VerticalLayout() {
    private val l = Logger.getLogger(JobsView::class.java)
    private final val grid = createGrid()
    private final val jobForm = createJobForm()

    private final val companyNameFilter = createCompanyNameFilter()

    @PostConstruct
    fun postConstruct() {
        setSizeFull()
        add(HorizontalLayout(companyNameFilter, Button("Add Job") { grid.asSingleSelect().clear(); onRowSelected(Job())}).apply { addClassName("toolbar") },
            HorizontalLayout(grid, jobForm).apply {
                addClassName("content")
                setFlexGrow(2.0, grid)
                setFlexGrow(1.0, jobForm)
                setSizeFull()
            })
        addClassName("jobs-list")

        grid.asSingleSelect().addValueChangeListener { event -> onRowSelected(event.value) }
    }

    private fun checkOrCreateContact() {
//        if (contactDataControl.contactAbsent(idToken.name)) {
//            QuarkusTransaction.run { contactDataControl.persist(Contact(email = idToken.name, givenName = idToken.getClaim("givenName"), familyName = idToken.getClaim("familyName"))) }
//        }
    }


    private fun closeEditor() {
        jobForm.jobSupplier = { null }
        jobForm.isVisible = false
        jobForm.removeClassName("editing")
    }


    private fun updateList() {
        if (companyNameFilter.value.isBlank()) {
            grid.setItems(jobDataControl.listAll())
        } else {
            grid.setItems(jobDataControl.list("company", companyNameFilter.value))
        }
    }

    private fun saveEvent(job: Job) {
        l.info("Save Event Called $job")
        QuarkusTransaction.run {
            job.contact = contactDataControl.findByEmail(authControl.email())!!
            l.info("Contact Id : ${job.contact.id}")
            jobDataControl.persist(job)
        }
        updateList()
        closeEditor()
    }

    private fun deleteEvent(job: Job) {
        l.info("Delete Event Called $job")
        job.contact = contactDataControl.findByEmail(authControl.email())!!
        jobDataControl.delete(job)
    }


    private fun createJobForm() = JobForm(this::saveEvent, this::deleteEvent, this::closeEditor).apply {
        width = "25em"
    }


    private fun createGrid() = Grid<Job>().apply {
        addClassName("job-grid")
        setSizeFull()
        addColumn { it.company }.setHeader("Company").apply { isSortable = true; isFrozen = true }
        addColumn { it.currentJobOpenings }.setHeader("Information About The Opening").apply { isSortable = true; isFrozen = true }
        addColumn { it.domains.joinToString { "," } }.setHeader("Job Domains").apply { isSortable = true; isFrozen = true }
        addColumn { it.slots.value }.setHeader("Number Of Openings").apply { isSortable = true; isFrozen = true }
        addColumn { it.contact.name() }.setHeader("Contact Information").apply { isFrozen = true }
        columns.forEach { it.setAutoWidth(true) }
        asSingleSelect().addValueChangeListener { it.value?.apply { onRowSelected(it.value) } }
    }

    private fun createCompanyNameFilter() = TextField().apply {
        placeholder = "Filter by Company"
        isClearButtonVisible = true
        valueChangeMode = ValueChangeMode.LAZY
    }

    private fun onRowSelected(job: Job) {
        jobForm.addClassName("editing")
        jobForm.isVisible = true
        jobForm.jobSupplier = { job }
    }
}