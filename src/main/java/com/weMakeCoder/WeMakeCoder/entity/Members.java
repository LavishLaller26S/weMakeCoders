package com.weMakeCoder.WeMakeCoder.entity;

import com.weMakeCoder.WeMakeCoder.enums.GroupRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;


@Entity
@Table(
        name = "group_members_table",
        indexes = {
                @Index(name="idx_group_id",columnList = "group_id"),
                @Index(name="idx_user_id",columnList = "user_id")
        },
        uniqueConstraints = @UniqueConstraint(columnNames = {"group_id","user_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"user","group"})
public class Members {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "seq_gen")
    @SequenceGenerator(
            name = "seq_gen",
            sequenceName = "mem_seq",
            allocationSize = 50
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="group_id",nullable = false,updatable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="user_id",nullable = false,updatable = false)
    private User user;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupRole role;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private Instant joinedAt;

    @Builder
    public Members(User user,Group group){
        this.group=group;
        this.user=user;
    }

    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof Members)){
            return false;
        }
        Members other=(Members)(o);
        return id!=null && this.id.equals(other.id);
    }

    @Override
    public int hashCode(){
        return getClass().hashCode();
    }

}


