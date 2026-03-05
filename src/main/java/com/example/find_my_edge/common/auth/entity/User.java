package com.example.find_my_edge.common.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    private String id;

    @Column(unique = true)
    private String email;

    private String password;

    private String username;

    private Instant createdAt;

    // getters setters
}