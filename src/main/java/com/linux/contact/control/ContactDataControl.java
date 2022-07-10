package com.linux.contact.control;

import com.linux.contact.entity.Contact;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

@ApplicationScoped
public class ContactDataControl implements PanacheRepository<Contact> {

    public Contact findOrCreate(@NotNull String email) {
        if (!contactPresent(email)) {
            var contact = new Contact();
            contact.email = email;
            return contact;
        }
        return findByEmail(email);
    }

    public boolean contactPresent(String email) {
        return findByEmail(email) != null;
    }

    @Transactional
    public Contact findByEmail(String email) {
        return find("email", email).firstResult();
    }
}