package com.weMakeCoder.WeMakeCoder.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(
        name="group_dsa_mapper_table",
        indexes = {
                @Index(name = "idx_group_id",columnList = "group_id"),
                @Index(name = "idx_dsa_id",columnList = "dsa_id"),
        },
        uniqueConstraints = @UniqueConstraint(name="unq_grp_dsa_id",columnNames = {"group_id","dsa_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"group","dsaSheet"})
public class GroupDsaMapper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="group_id",nullable = false,updatable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="dsa_id",nullable = false,updatable = false)
    private DsaSheet dsaSheet;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private Instant createdAt;

    @Builder
    public GroupDsaMapper(DsaSheet dsaSheet,Group group){
        this.group=group;
        this.dsaSheet=dsaSheet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GroupDsaMapper other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
