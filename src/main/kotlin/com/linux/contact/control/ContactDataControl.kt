package com.linux.contact.control

import com.linux.contact.entity.Contact
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ContactDataControl : PanacheRepository<Contact> {
    fun contactAbsent(email: String) = contactPresent(email).not()
    fun contactPresent(email: String) = findByEmail(email) != null
    fun findByEmail(email: String) = find("email", email).firstResult()
}