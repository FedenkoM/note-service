package com.test.project.noteservice.entity;


public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private String authority;
    Role(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}
