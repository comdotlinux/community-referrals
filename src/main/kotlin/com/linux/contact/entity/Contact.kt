package com.linux.contact.entity

import com.linux.jobs.entity.Job
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import javax.persistence.*
import javax.persistence.EnumType.STRING
import javax.validation.constraints.Email
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "contact")
data class Contact(
    @Email @NotBlank var email: String = "",
    @NotBlank @Min(3) var name: String = "",
    var mobileNumber: String = "",
    @Enumerated(STRING) var interest: Interest = Interest.NONE,
    @OneToMany var jobs: Set<Job> = emptySet()
) : PanacheEntity()

@Converter(autoApply = true)
enum class Interest : AttributeConverter<Interest, String> {
    MENTOR,
    MENTEE,
    NONE;

    override fun convertToDatabaseColumn(interest: Interest) = interest.name
    override fun convertToEntityAttribute(interest: String) = valueOf(interest)
}