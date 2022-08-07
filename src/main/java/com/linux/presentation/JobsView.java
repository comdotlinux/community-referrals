package com.linux.presentation;

import com.linux.auth.control.AuthenticationControl;
import com.linux.contact.control.ContactDataControl;
import com.linux.jobs.control.JobDataControl;
import com.linux.jobs.entity.Job;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import io.quarkus.narayana.jta.QuarkusTransaction;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.*;

import static com.linux.presentation.JobForm.*;

@Route(value = "", layout = MainView.class)
@PageTitle(value = "Jobs")
public class JobsView extends VerticalLayout {
    private static final Logger l = Logger.getLogger(JobsView.class);

    private final Grid<Job> grid = new Grid<>();
    private final TextField filterText = new TextField();
    private final JobDataControl jobDataControl;
    private final ContactDataControl contactDataControl;
    private final AuthenticationControl authenticationControl;

    private JobForm jobForm;

    @Inject
    public JobsView(JobDataControl jobDataControl, ContactDataControl contactDataControl, AuthenticationControl authenticationControl) {
        this.jobDataControl = jobDataControl;
        this.contactDataControl = contactDataControl;
        this.authenticationControl = authenticationControl;
        addClassName("jobs-list");
        setSizeFull();
        updateList();
        configureGrid();
        configureForm();
        add(getToolbar(), getContent());
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("jobs-grid");
        grid.setSizeFull();
        grid.addColumn((job) -> job.company).setHeader("Company").setSortable(true).setFrozen(true);
        grid.addColumn((job) -> job.currentJobOpenings).setHeader("Information About The Opening").setSortable(true).setFrozen(true);
        grid.addColumn((job) -> job.domains.stream().map((jobDomain) -> jobDomain.domain).collect(Collectors.joining(","))).setHeader("Job Domains").setSortable(true).setFrozen(true);
        grid.addColumn((job) -> job.slots.dbData).setHeader("Number Of Openings").setSortable(true).setFrozen(true);
        grid.addColumn((job) -> job.contact.name()).setHeader("Contact Information").setSortable(true).setFrozen(true);
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(event -> editJob(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by company...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        filterText.setValue("");

        Button addJobButton = new Button("Add Job");
        addJobButton.addClickListener(click -> addJob());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addJobButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateList() {
        filterText.getValue();
        grid.setItems(jobDataControl.findByCompany(filterText.getValue()));
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, jobForm);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, jobForm);
        content.addClassNames("content");
        content.setSizeFull();
        return content;
    }

    private void configureForm() {
        jobForm = new JobForm();
        jobForm.setWidth("25em");
        jobForm.addListener(SaveEvent.class, this::saveJob);
        jobForm.addListener(DeleteEvent.class, this::deleteJob);
        jobForm.addListener(CloseEvent.class, e -> closeEditor());
    }

    public void editJob(Job job) {
        if (job == null) {
            closeEditor();
            return;
        }
        if (job.contact == null || Objects.equals(contactDataControl.findByEmail(authenticationControl.email()).id, job.contact.id)) {
            jobForm.setJob(job);
            jobForm.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        jobForm.setJob(null);
        jobForm.setVisible(false);
        removeClassName("editing");
    }

    private void addJob() {
        grid.asSingleSelect().clear();
        editJob(new Job());
    }

    private void saveJob(SaveEvent event) {
        QuarkusTransaction.run(() -> {
            var job = event.job;
            if(!jobDataControl.isPersistent(job)) {
                l.warn("job from event is not persistent!" );
            }
            job.contact = contactDataControl.findOrCreate(authenticationControl.email());
            jobDataControl.persist(job);
            updateList();
            closeEditor();
        });
    }

    private void deleteJob(DeleteEvent event) {
        QuarkusTransaction.run(() -> jobDataControl.deleteById(event.job.id));
        updateList();
        closeEditor();
    }
}