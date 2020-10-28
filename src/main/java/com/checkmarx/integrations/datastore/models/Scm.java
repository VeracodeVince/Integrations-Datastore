package com.checkmarx.integrations.datastore.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "scms" , uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "base_url", "client_id", "client_secret"})})
public class Scm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "base_url")
    private String baseUrl;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret")
    private String clientSecret;
}