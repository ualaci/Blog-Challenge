package com.blogchallenge.blogchallenge.repositories;

import com.blogchallenge.blogchallenge.entities.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
