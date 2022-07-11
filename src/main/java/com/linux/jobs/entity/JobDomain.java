package com.linux.jobs.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Embeddable
@Table(name = "job_domains")
public class JobDomain {
    @NotBlank
    public String domain;
}