package com.linux.jobs.entity

import com.linux.contact.entity.Contact
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.EnumType.STRING
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity
class Job(
    @Column(name = "company") @NotBlank var company: String = "",
    @Column(name = "current_openings") @NotBlank var currentJobOpenings: String = "",
    @Column(name = "job_domains") @NotEmpty @OneToMany var domains: Set<JobDomain> = emptySet(),
    @NotNull @Enumerated(STRING) var slots: Slot = Slot.NONE,
    @OneToOne var contact: Contact = Contact(),
    @Column(name = "created_at") @CreationTimestamp() var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "modified_at") @UpdateTimestamp var modifiedAt: LocalDateTime = LocalDateTime.now()
) : PanacheEntity()

@Entity
@Table(name = "job_domain")
class JobDomain(
    @NotBlank var domain: String = "",
    @Column(name = "created_at") @CreationTimestamp() var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "modified_at") @UpdateTimestamp var modifiedAt: LocalDateTime = LocalDateTime.now()
) : PanacheEntity()

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