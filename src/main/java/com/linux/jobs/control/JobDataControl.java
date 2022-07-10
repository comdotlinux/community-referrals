package com.linux.jobs.control;

import com.linux.jobs.entity.Job;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

@ApplicationScoped
public class JobDataControl implements PanacheRepository<Job> {
    public List<Job> findByCompany(String parameter) {
        if(parameter == null || "".equals(parameter)) {
            return listAll();
        }

        return find("Select j from Job j where lower(j.company) like ?1", "%" + parameter.toLowerCase() + "%").list();
    }
}