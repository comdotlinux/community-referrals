package com.linux.presentation

import com.linux.jobs.entity.Job
import com.linux.jobs.entity.Slot
import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.ComponentEventBus
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.shared.Registration


@Suppress("unused")
class JobForm : FormLayout() {
    var job: Job? = null

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
            if (job != null) {
                binder.writeBean(job)
                fireEvent(SaveEvent(this, job, eventBus))
            }
        }.apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY); addClickShortcut(Key.ENTER)
        },
        Button("Delete") { fireEvent(DeleteEvent(this, job, eventBus)) }.apply { addThemeVariants(ButtonVariant.LUMO_ERROR) },
        Button("Close") { fireEvent(CloseEvent(this, null, eventBus)) }.apply { addThemeVariants(ButtonVariant.LUMO_TERTIARY); addClickShortcut(Key.ESCAPE) }
    )
}

open class JobFormEvent(private val jobForm: JobForm, private val job: Job?, private val eventBus: ComponentEventBus) : ComponentEvent<JobForm>(jobForm, false) {
    fun <T : ComponentEvent<*>> addListener(eventType: Class<T>, listener: ComponentEventListener<T>): Registration = eventBus.addListener(eventType, listener)
}

data class SaveEvent(val jobForm: JobForm, val job: Job?, val eventBus: ComponentEventBus) : JobFormEvent(jobForm, job, eventBus)
data class DeleteEvent(val jobForm: JobForm, val job: Job?, val eventBus: ComponentEventBus) : JobFormEvent(jobForm, job, eventBus)
data class CloseEvent(val jobForm: JobForm, val job: Job?, val eventBus: ComponentEventBus) : JobFormEvent(jobForm, job, eventBus)