package com.linux.jobs.entity

import com.linux.contact.entity.Contact
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import javax.persistence.*
import javax.persistence.EnumType.STRING
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity
data class Job(
    @NotBlank var company: String = "",
    @NotBlank var currentJobOpenings: String = "",
    @NotEmpty @OneToMany var domains: Set<JobDomain> = emptySet(),
    @NotNull @Enumerated(STRING) var slots: Slot = Slot.NONE,
    @OneToOne var contact: Contact = Contact()
) : PanacheEntity()

@Entity
@Table(name = "job_domain")
data class JobDomain(@NotBlank var domain: String = "") : PanacheEntity()

@Converter(autoApply = true)
enum class Slot(val value: String) : AttributeConverter<Slot, String> {
    ONE("1"),
    TWO("2"),
    THREE("3"),
    MANY("Many"),
    NONE("None");

    override fun convertToDatabaseColumn(slot: Slot) = slot.name
    override fun convertToEntityAttribute(slot: String) = valueOf(slot)
}