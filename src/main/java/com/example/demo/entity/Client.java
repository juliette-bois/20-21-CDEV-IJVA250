package com.example.demo.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

/**
 * Entity représentant un client.
 */
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "client")
    private Set<Facture> factures;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String nom;

    @Column
    private LocalDate dateNaissance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public Set<Facture> getFactures() {
        return factures;
    }

    // convert date de naissance to age
    public Integer getAge() {
        LocalDate dateBirthday = this.getDateNaissance();
        LocalDate today = LocalDate.now();
        Integer yearBirthday = dateBirthday.getYear();
        Integer year = today.getYear();
        return year - yearBirthday;
    }
}
