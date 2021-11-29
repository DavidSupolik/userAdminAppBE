package com.rohlik.useradminbe.models;

import java.util.Date;

public class User implements Cloneable{
    private Integer id;
    private String firstName;
    private String surname;
    private String status;
    private String email;
    private int telephone;
    private Date creationDate;

    public User() {
    }

    public User(int id, String firstName, String surname, String status, String email, int telephone, Date creationDate) {
        this.id = id;
        this.firstName = firstName;
        this.surname = surname;
        this.status = status;
        this.email = email;
        this.telephone = telephone;
        this.creationDate = creationDate;
    }

    @Override
    public Object clone() {
        User clone = null;
        try
        {
            clone = (User) super.clone();

            //Copy new date object to cloned method
            clone.setCreationDate((Date) this.getCreationDate().clone());
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
        return clone;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

}
