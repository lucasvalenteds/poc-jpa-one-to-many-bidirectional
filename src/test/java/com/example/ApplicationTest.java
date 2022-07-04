package com.example;

import com.example.document.Document;
import com.example.document.DocumentRepository;
import com.example.person.Person;
import com.example.person.PersonRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApplicationTest {

    @Container
    private static final PostgreSQLContainer<?> CONTAINER =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres"));

    @DynamicPropertySource
    private static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", CONTAINER::getUsername);
        registry.add("spring.datasource.password", CONTAINER::getPassword);
    }

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private PersonRepository personRepository;

    @Test
    @Order(1)
    void creatingDocument() {
        final var document = new Document();
        document.setCode("XD892342");

        final var documentSaved = documentRepository.save(document);

        assertNotNull(documentSaved.getId());
        assertEquals(document.getCode(), documentSaved.getCode());
    }

    @Test
    @Order(2)
    void creatingPerson() {
        final var person = new Person();
        person.setName("John Smith");

        final var personSaved = personRepository.save(person);

        assertNotNull(person.getId());
        assertEquals(person.getName(), personSaved.getName());
        assertEquals(person.getDocuments(), personSaved.getDocuments());
    }

    @Test
    @Order(3)
    void assigningDocumentsToPerson() {
        // Creating two documents with different codes
        final var document1 = new Document();
        document1.setCode("XYZ00001");

        final var document2 = new Document();
        document2.setCode("XYZ00002");

        final var documents = new HashSet<Document>();
        for (var document : documentRepository.saveAll(Set.of(document1, document2))) {
            documents.add(document);
        }

        // Creating person and assigning to the documents
        final var person = new Person();
        person.setName("John Smith");
        person.setDocuments(documents);

        // Asserting the assignment works
        final var personSaved = personRepository.save(person);

        assertNotNull(personSaved.getId());
        assertEquals(person.getName(), personSaved.getName());
        assertThat(person.getDocuments())
                .containsExactlyElementsOf(documents);
    }

    @Test
    @Order(4)
    @Transactional
    void assigningDocumentsToExistingPerson() {
        // Creating one person without documents assigned
        final var person = new Person();
        person.setName("Mary Jane");
        person.setDocuments(new HashSet<>());
        final var personSaved = personRepository.save(person);

        // Creating two documents and assigning them to the same person
        final var document1 = new Document();
        document1.setCode("PAC82342");
        document1.setPerson(personSaved);
        final var document1Saved = documentRepository.save(document1);

        final var document2 = new Document();
        document2.setCode("PAC57123");
        document2.setPerson(personSaved);
        final var document2Saved = documentRepository.save(document2);

        // Finding documents assigned to a person and asserting it worked
        final var documents = documentRepository.findAllByPersonId(personSaved.getId());

        // Many-to-One fetching relationship eagerly
        assertEquals(personSaved, document1Saved.getPerson());
        assertEquals(personSaved, document2Saved.getPerson());

        // One-to-Many fetching relationship lazily when method is annotated with Transactional
        assertThat(documents)
                .containsExactlyInAnyOrderElementsOf(Set.of(document1Saved, document2Saved));
    }
}
