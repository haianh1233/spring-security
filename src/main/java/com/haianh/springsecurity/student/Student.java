package com.haianh.springsecurity.student;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Student implements Serializable {

    @Id
    private int id;
    private String name;
    private String email;
    private String password;

}
