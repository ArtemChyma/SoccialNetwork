package org.example.socialnetworka.users;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    public User() {
    }

    public User(Long id, String firstName, String secondName,
                String email, String password, String username, String idImagePath) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.password = password;
        this.username = username;
        this.idImagePath = idImagePath;
    }

//    public User(Long id, String firstName, String secondName,
//                String email, String password, String username) {
//        this.id = id;
//        this.firstName = firstName;
//        this.secondName = secondName;
//        this.email = email;
//        this.password = password;
//        this.username = username;
//    }

    public User(String firstName, String secondName,
                String email, String username) {
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.username = username;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String firstName;
    @Column
    private String secondName;
    @Column(unique = true)
    private String email;
    private String password;
    @Column(unique = true)
    private String username;
//    @Column(unique = true)
    private String idImagePath;

    public String getIdImagePath() {
        return idImagePath;
    }

    public void setIdImagePath(String idImagePath) {
        this.idImagePath = idImagePath;
    }
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
