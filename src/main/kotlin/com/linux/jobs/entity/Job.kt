package com.linux.jobs.entity

import com.linux.contact.entity.Contact
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.EnumType.STRING
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity
class Job(
    @Id @GeneratedValue var id: Long? = null,
    @Column(name = "company") @NotBlank var company: String = "",
    @Column(name = "current_openings") @NotBlank var currentJobOpenings: String = "",
    @ElementCollection
    @CollectionTable @NotEmpty var domains: MutableSet<JobDomain> = mutableSetOf(),
    @NotNull @Enumerated(STRING) var slots: Slot = Slot.NONE,
    @ManyToOne @JoinColumn(name="contact_id", nullable=false) var contact: Contact = Contact(),
    @Column(name = "created_at") @CreationTimestamp() var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "modified_at") @UpdateTimestamp var modifiedAt: LocalDateTime = LocalDateTime.now()
)

@Embeddable
@Table(name = "job_domain")
class JobDomain(
    @NotBlank var domain: String = "",
    @Column(name = "created_at") @CreationTimestamp() var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "modified_at") @UpdateTimestamp var modifiedAt: LocalDateTime = LocalDateTime.now()
)

@Converter(autoApply = true)
enum class Slot(val value: String) : AttributeConverter<Slot, String> {
    ONE("1"), TWO("2"), THREE("3"), MANY("Many"), NONE("None");

    override fun convertToDatabaseColumn(slot: Slot) = slot.name
    override fun convertToEntityAttribute(slot: String) = valueOf(slot)
}