package com.haianh.springsecurity.appuser;

import com.haianh.springsecurity.student.Student;
import com.haianh.springsecurity.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<Student> student = studentRepository.findStudentByName(username);
        student.orElseThrow(() -> new UsernameNotFoundException(String.format("%s not found", username) ));
        return student.map(MyUserDetails::new).get();

    }
}
