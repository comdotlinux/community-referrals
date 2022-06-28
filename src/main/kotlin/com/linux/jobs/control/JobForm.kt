package com.linux.jobs.control

import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField


@Suppress("unused")

class JobForm : FormLayout() {
    init {

        addClassName("job-form")
        add(
            TextField("Company"),
            TextField("Information About The Opening"),
            TextField("Job Domains"),
            TextField("Number Of Openings"),
            createButtonLayout()
        )
    }

    private fun createButtonLayout() = HorizontalLayout(
        Button("Save").apply { addThemeVariants(ButtonVariant.LUMO_PRIMARY); addClickShortcut(Key.ENTER) },
        Button("Delete").apply { addThemeVariants(ButtonVariant.LUMO_ERROR) },
        Button("Close").apply { addThemeVariants(ButtonVariant.LUMO_TERTIARY); addClickShortcut(Key.ESCAPE) }
    )
}