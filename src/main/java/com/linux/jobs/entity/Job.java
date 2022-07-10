package com.linux.jobs.entity;

import com.linux.contact.entity.Contact;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.*;
import java.time.*;
import java.util.*;

import static javax.persistence.EnumType.STRING;

@Entity
@Table(name = "job")
public class Job {
    @Id
    @GeneratedValue
    public Long id;


    @Column(name = "company")
    @NotBlank
    public String company;


    @Column(name = "current_openings")
    @NotBlank
    public String currentJobOpenings;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable
    @NotEmpty
    public Set<JobDomain> domains;

    @NotNull
    @Enumerated(STRING)
    public Slot slots;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contact_id", nullable = false)
    public Contact contact;


    @Column(name = "created_at")
    @CreationTimestamp()
    public LocalDateTime createdAt;

    @Column(name = "modified_at")
    @UpdateTimestamp
    public LocalDateTime modifiedAt;
}