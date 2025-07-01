package com.dao.momentum.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Long empId;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(Long empId, List<GrantedAuthority> authorities) {
        this.empId = empId;
        this.authorities = authorities;
    }

    public Long getEmpId() {
        return empId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return String.valueOf(empId); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
