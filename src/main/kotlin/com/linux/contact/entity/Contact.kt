package com.linux.contact.entity

import com.linux.jobs.entity.Job
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import javax.persistence.*
import javax.persistence.EnumType.STRING
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "contact")
class Contact(
    @Id @GeneratedValue var id: Long? = null,
    @Email @NotBlank var email: String = "",
    var givenName: String? = null,
    var familyName: String? = null,
    @Enumerated(STRING) var interest: Interest = Interest.NONE,
    @OneToMany(mappedBy = "contact") var jobs: MutableSet<Job> = mutableSetOf(),
    @Column(name = "created_at") @CreationTimestamp() var createdAt: LocalDateTime = LocalDateTime.now(),
    @Column(name = "modified_at") @UpdateTimestamp var modifiedAt: LocalDateTime = LocalDateTime.now()
) {
    @Transient
    fun name() = "$givenName $familyName"
}

@Converter(autoApply = true)
enum class Interest : AttributeConverter<Interest, String> {
    MENTOR, MENTEE, NONE;

    override fun convertToDatabaseColumn(interest: Interest) = interest.name
    override fun convertToEntityAttribute(interest: String) = valueOf(interest)
}