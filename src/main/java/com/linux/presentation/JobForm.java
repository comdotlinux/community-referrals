package com.linux.presentation;

import com.linux.jobs.entity.*;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.*;
import com.vaadin.flow.data.binder.*;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.shared.Registration;
import org.jboss.logging.Logger;

import java.util.*;
import java.util.stream.*;

public class JobForm extends FormLayout {
    private static final Logger l = Logger.getLogger(JobForm.class);

    TextField company = new TextField("Company");
    TextField currentJobOpenings = new TextField("Information About The Opening");
    TextField domains = new TextField("Job Domains");
    ComboBox<Slot> slots = new ComboBox<>("Number Of Openings");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");
    Binder<Job> binder = new BeanValidationBinder<>(Job.class);

    private Job job;

    public JobForm() {
        addClassName("contact-form");
        binder.forField(company).bind(j -> j.company, (j, company) -> j.company = company);
        binder.forField(currentJobOpenings).bind(j -> j.currentJobOpenings, (j, currentJobOpenings) -> j.currentJobOpenings = currentJobOpenings);
        binder.forField(domains).withConverter(new JobDomainConverter()).bind(j -> j.domains, (j, domains) -> j.domains = domains);

        slots.setItems(Arrays.stream(Slot.values()).sorted().collect(Collectors.toList()));
        slots.setItemLabelGenerator(Slot::asString);
        binder.forField(slots).withConverter(new SlotConverter()).bind(j -> Slot.asString(j.slots), (j, value) -> j.slots = Slot.fromString(value));

        add(company, currentJobOpenings, domains, slots, createButtonsLayout());
    }

    public void setJob(Job job) {
        this.job = job;
        binder.readBean(job);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, job)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(job);
            fireEvent(new SaveEvent(this, job));
        } catch (ValidationException e) {
            l.warn("validateAndSave: Validation Issue", e);
        }
    }

    // Events
    public static abstract class JobFormEvent extends ComponentEvent<JobForm> {
        public final Job job;

        protected JobFormEvent(JobForm source, Job job) {
            super(source, false);
            this.job = job;
        }
    }

    public static class SaveEvent extends JobFormEvent {
        SaveEvent(JobForm source, Job contact) {
            super(source, contact);
        }
    }

    public static class DeleteEvent extends JobFormEvent {
        DeleteEvent(JobForm source, Job contact) {
            super(source, contact);
        }

    }

    public static class CloseEvent extends JobFormEvent {
        CloseEvent(JobForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }


    static class JobDomainConverter implements Converter<String, Set<JobDomain>> {
        @Override
        public Result<Set<JobDomain>> convertToModel(String value, ValueContext context) {
            if (value == null || "".equals(value)) {
                return Result.ok(Set.of());
            }

            return Result.ok(Arrays.stream(value.split(",")).map((jobDomainValue) -> JobDomainBuilder.create().withDomain(jobDomainValue.strip()).build()).collect(Collectors.toSet()));
        }

        @Override
        public String convertToPresentation(Set<JobDomain> value, ValueContext context) {
            return value == null ? "" : value.stream().map((jobDomain) -> jobDomain.domain.strip()).collect(Collectors.joining(", "));
        }
    }

    static class SlotConverter implements Converter<Slot, String> {
        @Override
        public Result<String> convertToModel(Slot slot, ValueContext context) {
            return Result.ok(Slot.asString(slot));
        }

        @Override
        public Slot convertToPresentation(String value, ValueContext context) {
            return Slot.fromString(value);
        }
    }
}