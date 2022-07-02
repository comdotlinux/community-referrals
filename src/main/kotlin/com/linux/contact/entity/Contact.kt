package com.linux.contact.entity

import com.linux.jobs.entity.Job
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.EnumType.STRING
import javax.validation.constraints.Email
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "contact")
class Contact(
    @Email @NotBlank var email: String = "",
    @NotBlank @Min(3) @Max(1000) var name: String = "",
    @Enumerated(STRING) var interest: Interest = Interest.NONE,
    @OneToMany var jobs: Set<Job> = emptySet(),
    @Column(name = "created_at") @CreationTimestamp() var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "modified_at") @UpdateTimestamp var modifiedAt: LocalDateTime = LocalDateTime.now()
) : PanacheEntity()

@Converter(autoApply = true)
enum class Interest : AttributeConverter<Interest, String> {
    MENTOR,
    MENTEE,
    NONE;

    override fun convertToDatabaseColumn(interest: Interest) = interest.name
    override fun convertToEntityAttribute(interest: String) = valueOf(interest)
}