package com.blogchallenge.blogchallenge.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post", schema = "blog")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Post {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String title;
    private String body;
    private boolean isReprocessed;
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL )
    private List<History> history = new ArrayList<>();

    public void addHistory(History history) {
        this.history.add(history);
    }
}
