package com.haianh.springsecurity.student;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
public class Student implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "seq")
    @SequenceGenerator(name="seq", initialValue=5, allocationSize=1)
    private int id;
    @Column(name = "name", unique = true)
    private String name;
    private String email;
    private String role;
    private String password;
    private String phone;
    private String pictureUrl;


}
