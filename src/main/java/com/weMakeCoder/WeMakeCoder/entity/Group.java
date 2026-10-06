package com.weMakeCoder.WeMakeCoder.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Entity
@Table(name = "groups_table")
@Getter
@ToString(exclude = {"admin","passwordHash"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="group_name",unique = true, nullable = false, length = 100)
    private String groupName;

    @Setter
    @Column(nullable = false, length = 100)
    private String displayName;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "admin_id", nullable = false,updatable = false)
    private User admin;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @Setter
    @Column(name = "joining_code", nullable = false, unique = true, length = 20)
    private String groupCode;

    @Setter
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Setter
    @Column(nullable = false,updatable = false)
    private Long membersCount=1L;

    @Builder
    public Group(String groupName,String displayName,User admin,String passwordHash){
        this.groupName=normalize(groupName);
        this.displayName=displayName;
        this.admin=admin;
        this.passwordHash=passwordHash;
    }

    public void setGroupName(String value){
        this.groupName=normalize(value);
    }

    private static String normalize(String value){
        return value==null?null:value.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Group other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
