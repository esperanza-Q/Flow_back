package org.example.flow.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "recommendVisitLog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendVisitLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="recommendVisitLog_id", nullable = false)
    private Long recommendVisitLogId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    private LocalDate weekStart;

    private Integer visitCount;

}
