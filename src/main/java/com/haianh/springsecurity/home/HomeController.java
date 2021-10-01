package com.haianh.springsecurity.home;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.haianh.springsecurity.appuser.MyUserDetailService;
import com.haianh.springsecurity.appuser.MyUserDetails;
import com.haianh.springsecurity.jwt.JwtConfig;
import com.haianh.springsecurity.jwt.JwtUtil;
import com.haianh.springsecurity.model.AuthenticationRequest;
import com.haianh.springsecurity.model.AuthenticationResponse;
import com.haianh.springsecurity.model.GoogleAuthenticationRequest;
import com.haianh.springsecurity.student.Student;
import com.haianh.springsecurity.student.StudentRepository;
import jdk.nashorn.internal.ir.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import static org.springframework.http.HttpHeaders.*;

@RestController
public class HomeController {

    private final AuthenticationManager authenticationManager;
    private final MyUserDetailService myUserDetailService;
    private final JwtUtil jwtUtil;
    private final StudentRepository studentRepository;
    private final JwtConfig jwtConfig;

    @Autowired
    public HomeController(AuthenticationManager authenticationManager,
                          MyUserDetailService myUserDetailService,
                          JwtUtil jwtUtil,
                          StudentRepository studentRepository,
                          JwtConfig jwtConfig) {
        this.authenticationManager = authenticationManager;
        this.myUserDetailService = myUserDetailService;
        this.jwtUtil = jwtUtil;
        this.studentRepository = studentRepository;
        this.jwtConfig = jwtConfig;
    }

    @GetMapping("/")
    public String home() {
        return ("<h1>Welcome home </h1>");
    }

    @GetMapping("/user")
    public ResponseEntity<?> user() {
        ResponseEntity res = ResponseEntity.ok().body("<h1>Welcome user </h1>");
        return res;
    }

    @GetMapping("/admin")
    public String admin() {
        return ("<h1>Welcome admin </h1>");
    }

    @PostMapping("/authentication")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws BadCredentialsException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Incorrect username or password");
        }

        final UserDetails userDetails = myUserDetailService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/authentication/google")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody GoogleAuthenticationRequest authenticationRequest) throws GeneralSecurityException, IOException {
        NetHttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new GsonFactory();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList("431681257434-1makcgok3vs3tnivthtvcbnt2fcc1aul.apps.googleusercontent.com"))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        GoogleIdToken idToken = verifier.verify(authenticationRequest.getToken());
        System.out.println("Token " + authenticationRequest);
        System.out.println("Id token:" + idToken);

        String jwt = null;
        UserDetails userDetails = null;
        ResponseEntity t = null;

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            System.out.println(email);
            System.out.println(emailVerified);
            System.out.println(name);
            System.out.println(pictureUrl);
            System.out.println(locale);
            System.out.println(familyName);
            System.out.println(givenName);

            try {
                userDetails = myUserDetailService.loadUserByUsername(name.replace(" ", ""));
                jwt = jwtUtil.generateToken(userDetails);
            }catch (UsernameNotFoundException e) {
                System.out.println(e.getMessage());
                Student student = new Student();
                student.setEmail(email);
                student.setName(name.replace(" ", ""));
                student.setRole("USER");
                student.setPictureUrl(pictureUrl);

                studentRepository.saveAndFlush(student);
                userDetails = myUserDetailService.loadUserByUsername(name.replace(" ", ""));
                jwt = jwtUtil.generateToken(userDetails);
                System.out.println("Create new student and return token");
            }
        }
            t = ResponseEntity.ok(new AuthenticationResponse(jwt));
        return t;
    }
}
