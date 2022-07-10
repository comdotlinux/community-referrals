package com.linux.contact.entity;

import com.linux.jobs.entity.Job;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.*;
import java.time.*;
import java.util.*;

import static javax.persistence.EnumType.STRING;

@Entity
@Table(name = "contact")
public class Contact {

    @Id @GeneratedValue
    public Long id;

    @Email
    @NotBlank
    public String email;

    public String givenName;

    public String familyName;


    @Enumerated(STRING)
    public Interest interest;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "contact")
    public Set<Job> jobs;


    @Column(name = "created_at")
    @CreationTimestamp
    public LocalDateTime createdAt;

    @Column(name = "modified_at")
    @UpdateTimestamp
    public LocalDateTime modifiedAt;


    @Transient
    public String name() {
        return givenName + " " + familyName;
    }

}

@Converter(autoApply = true)
enum Interest implements AttributeConverter<Interest, String> {
    MENTOR, MENTEE, NONE;
    @Override
    public String convertToDatabaseColumn(Interest interest) {
        return interest.name();
    }

    @Override
    public Interest convertToEntityAttribute(String interest) {
        return Interest.valueOf(interest);
    }
}