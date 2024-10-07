package org.example.socialnetworka.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UserDAO {

    private Long id;
    @NotEmpty(message = "This area cannot be empty")
    @Size(min = 2, max = 20, message = "The length of the name should be between 2 and 20 characters")
    private String firstName;
    private String secondName;
    @Email(message = "Invalid email format")
    private String email;
    @Size(min = 8, message = "Password should contain at least 8 characters")
    private String password;
    @Size(min = 6, message = "Username should consist of at least 6 characters")
    private String username;
    private String idImagePath;

//    public UserDAO(Long id, String firstName, String secondName, String email, String password, String username) {
//        this.id = id;
//        this.firstName = firstName;
//        this.secondName = secondName;
//        this.email = email;
//        this.password = password;
//        this.username = username;
//    }

    public UserDAO(Long id, String firstName, String secondName, String email, String password, String username, String idImagePath) {
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.email = email;
        this.password = password;
        this.username = username;
        this.idImagePath = idImagePath;
    }
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getEmail() {
        return email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
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

    public String getIdImagePath() {
        return idImagePath;
    }

    public void setIdImagePath(String idImagePath) {
        this.idImagePath = idImagePath;
    }
}
