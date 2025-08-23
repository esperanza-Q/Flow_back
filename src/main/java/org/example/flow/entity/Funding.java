package org.example.flow.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "funding")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Funding {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="funding_id", nullable = false)
    private Long fundingId;

    private String name;

    private String organizer;

    private Integer goalSeed;

    private Integer nowSeed;

    private String image;

    private LocalDate endDate;

    private String introduction;

    private String review;

    @Enumerated(EnumType.STRING)
    private CATEGORY category;

    @Enumerated(EnumType.STRING)
    private STATUS status;



    public enum CATEGORY {
        ENVIRONMENT,
        CHILD,
        OLD,
        SAFE
    }

    public enum STATUS {
        INPROGRESS,
        FINISHED
    }

    @OneToMany(mappedBy = "funding", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Funded> fundeds;
}
