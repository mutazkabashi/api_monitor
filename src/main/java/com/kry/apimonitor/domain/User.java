package com.kry.apimonitor.domain;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

/**
 * @author Mutaz Hussein Kabashi
 * @version 1.0
 */
@Entity
@Table(name = "user")
@DataObject(generateConverter = true, publicConverter = false)
public class User extends BaseModel {
    @Id
    @GeneratedValue
    //FIXME Id shouldnt have setter method but we will add here as a waorkaround
    private Integer id;

    //@Size(min=13, max=13)
    @Email
    @NotNull(message = "email shouldn't be  be null")
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotNull
    @Size(max = 100)
    @NotBlank(message = "firstName shouldn't  be empty")
    @Column(name = "first_name", nullable = false)
    private String firstName;


    //FIXME emunarator (GET,POST,...etc)
    @NotNull
    @Size(min = 3)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Size(min = 3)
    @Column(name = "password", nullable = false)
    private String password;


    //List<WebService> webServices ;


    public Integer getId() {
        return id;
    }

    //FIXME workaroud solution
    public void setId(Integer id) {
        this.id = id;
    }


    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        UserConverter.toJson(this, json);
        return json;
    }

    public User() {
    }

    public User(String email, String firstName, String lastName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public User(JsonObject json) {
        UserConverter.fromJson(json, this);
    }


    public JsonArray ListToJson(List<User> users) {
        JsonArray result = new JsonArray();
        for (User user : users) {
            JsonObject json = new JsonObject();
            UserConverter.toJson(user, json);
            result.add(json);
        }
        return result;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return id.equals(user.id) &&
                email.equals(user.email) &&
                firstName.equals(user.firstName) &&
                lastName.equals(user.lastName) &&
                password.equals(user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, email, firstName, lastName, password);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}