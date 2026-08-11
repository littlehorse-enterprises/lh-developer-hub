package org.example.model;

/**
 * A simple POJO returned by the {@code fetch-user} task. LittleHorse
 * automatically serializes it to a {@code JSON_OBJ} variable value.
 */
public class User {

    public String email;
    public String title;
    public int age;

    public User() {}

    public User(String email, String title, int age) {
        this.email = email;
        this.title = title;
        this.age = age;
    }
}
