package com.haianh.springsecurity.student;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
public class Student implements Serializable {

    @Id
    private int id;
    private String name;
    private String email;
    private String role;
    private String password;
    private String phone;
    private String pictureUrl;


}
