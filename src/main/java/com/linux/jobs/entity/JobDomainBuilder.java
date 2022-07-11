package com.linux.jobs.entity;

public class JobDomainBuilder {

    public static JobDomainBuilder create() {
        return new JobDomainBuilder();
    }
    private String domain;

    public JobDomainBuilder withDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public JobDomain build() {
        var jobDomain = new JobDomain();
        jobDomain.domain = domain;
        return jobDomain;
    }
}