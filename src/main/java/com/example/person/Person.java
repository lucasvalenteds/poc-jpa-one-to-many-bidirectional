package com.example.person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PERSON")
@Data
@NoArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERSON_ID_SEQUENCE_GENERATOR")
    @SequenceGenerator(name = "PERSON_ID_SEQUENCE_GENERATOR", sequenceName = "PERSON_ID_SEQUENCE", allocationSize = 1)
    @Column(name = "PERSON_ID")
    private Long id;

    @Column(name = "PERSON_NAME")
    private String name;
}