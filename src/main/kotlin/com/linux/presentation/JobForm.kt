package com.linux.presentation

import com.linux.jobs.entity.Job
import com.linux.jobs.entity.Slot
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.BeanValidationBinder
import org.jboss.logging.Logger


@Suppress("unused")
class JobForm(val saveEvent: (job: Job) -> Unit, val deleteEvent: (job: Job) -> Unit, val closeEvent: () -> Unit) : FormLayout() {
    var jobSupplier: () -> Job? = { null }

    private val l = Logger.getLogger(JobForm::class.java)
    init {
        val binder = BeanValidationBinder(Job::class.java)
        addClassName("job-form")
        add(
            TextField("Company").apply { binder.bind(this, "company") },
            TextField("Information About The Opening").apply { binder.bind(this, "currentJobOpenings") },
            TextField("Job Domains").apply { binder.forField(this).withConverter({ it.split(",").toSet() }, { it.joinToString(",") }) },
            TextField("Number Of Openings").apply { binder.forField(this).withConverter({ Slot.valueOf(it) }, { it.value }) },
            createButtonLayout(binder)
        )
    }

    private fun createButtonLayout(binder: BeanValidationBinder<Job>) = HorizontalLayout(
        Button("Save") {
            l.info("save event called")
            val job = Job()
            binder.writeBean(job)
            saveEvent(job)
            jobSupplier = { job }
        }.apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY); addClickShortcut(Key.ENTER)
        },
        Button("Delete") { val job = Job(); binder.writeBean(job); deleteEvent(job) }.apply { addThemeVariants(ButtonVariant.LUMO_ERROR) },
        Button("Close") { closeEvent() }.apply { addThemeVariants(ButtonVariant.LUMO_TERTIARY); addClickShortcut(Key.ESCAPE) }
    )
}