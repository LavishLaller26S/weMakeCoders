package com.weMakeCoder.WeMakeCoder.entity;

import com.weMakeCoder.WeMakeCoder.enums.Source;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name="dsa_table",
        indexes = {
                @Index(name = "idx_user_id",columnList = "user_id"),
                @Index(name = "idx_user_id",columnList = "source"),
        },
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","url"}))

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(exclude = {"user"})
public class DsaSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_gen")
    @SequenceGenerator(
            name = "seq_gen",
            sequenceName = "user_seq",
            allocationSize = 50
    )
    private Long id;

    @Column(nullable = false,length=255)
    private String title;

    @Column(nullable = false,unique = true)
    private String url;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Source source;

    @Setter
    @Column(columnDefinition = "TEXT",length = 5000)
    private String notes;

    @OneToMany(mappedBy = "dsa")
    private List<GroupDsaMapper> groupDsaMappersList;   // here focus on this when child get deleted (
    // if it's in the main memory then it is dangling data (means data is not present but still present))

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="user_id",nullable = false)
    private User user;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private Instant submittedAt;

    @UpdateTimestamp
    @Column(nullable = false,updatable = false)
    private Instant updatedAt;

    @Builder
    public DsaSheet(String title, String url, Source source, String notes, User user){
        this.title=normalize(title);
        this.url=normalize(url);
        this.source=source;
        this.notes=notes;
        this.user=user;
    }


    public void setTitle(String value){
        this.title=normalize(value);
    }
    public void setUrl(String value){
        this.url=normalize(value);
    }


    public String normalize(String value){
        return value==null?null:value.trim().toLowerCase(Locale.ROOT);
    }
    @Override
    public boolean equals(Object o){
        if(o==null) return false;

        if(!(o instanceof DsaSheet other)){
            return false;
        }
        return id!=null && other.id .equals(this.id);
    }

    @Override
    public int hashCode(){
        return getClass().hashCode();
    }

}
