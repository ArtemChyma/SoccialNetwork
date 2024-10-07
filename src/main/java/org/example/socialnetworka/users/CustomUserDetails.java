package org.example.socialnetworka.users;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private String email;
    private String firstName;
    private String secondName;
    private String password;
    private String username;
    private String idImagePath;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String email, String firstName, String secondName, String password,
                             Collection<? extends GrantedAuthority> authorities, String username, String idImagePath) {
        this.email = email;
        this.firstName = firstName;
        this.secondName = secondName;
        this.password = password;
        this.authorities = authorities;
        this.username = username;
        this.idImagePath = idImagePath;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
