package com.linux.jobs.control

import com.linux.jobs.entity.Job
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class JobDataControl : PanacheRepository<Job>