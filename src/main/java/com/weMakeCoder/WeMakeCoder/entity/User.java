package com.weMakeCoder.WeMakeCoder.entity;

import com.weMakeCoder.WeMakeCoder.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name = "users"
        )
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"passwordHash"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_name", nullable = false, unique = true, length = 50)
    private String userName;

    @Setter
    @Column(nullable = false, length = 50)
    private String displayName;

    @Setter
    @Column(nullable = false , length = 10)
    private LocalDate dateOfBirth;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Setter
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, unique = true, length = 100)
    private String mail;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private Instant joinedAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @Setter
    @Column(nullable = false)
    private boolean active = true;

    @Builder
    private User(String userName,String displayName,String mail,String passwordHash,
                 LocalDate dateOfBirth,Gender gender){
        this.userName=normalize(userName);
        this.displayName=displayName;
        this.mail=normalize(mail);
        this.passwordHash=passwordHash;
        this.dateOfBirth=dateOfBirth;
        this.gender=gender;
    }


    public void setUserName(String name){
        this.userName= normalize(name);
    }
    public void setMail(String mail){
        this.mail= normalize(mail);
    }

    private static String normalize(String value){
        return value==null?null:value.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}