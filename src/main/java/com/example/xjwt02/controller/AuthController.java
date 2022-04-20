package com.example.xjwt02.controller;

import com.example.xjwt02.entity.User;
import com.example.xjwt02.models.LoginCredentials;
import com.example.xjwt02.repository.UserRepository;
import com.example.xjwt02.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController // Marks the class a rest controller
@RequestMapping("/api/auth") // Requests made to /api/auth/anything will be handles by this class
public class AuthController {

    // Injecting Dependencies
    @Autowired
    UserRepository userRepo;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    PasswordEncoder passwordEncoder;

    // Defining the function to handle the POST route for registering a user
    @PostMapping("/register")
    public Map<String, Object> registerHandler(@RequestBody User user){
        // Encoding Password using Bcrypt
        String encodedPass = passwordEncoder.encode(user.getPassword());

        // Setting the encoded password
        user.setPassword(encodedPass);

        // Persisting the User Entity to H2 Database
        user = userRepo.save(user);

        // Generating JWT
        String token = jwtUtil.generateToken(user.getEmail());

        // Responding with JWT
        return Collections.singletonMap("jwt-token", token);
    }

    // Defining the function to handle the POST route for logging in a user
    @PostMapping("/login")
    public Map<String, Object> loginHandler(@RequestBody LoginCredentials body){
        try {
            // Creating the Authentication Token which will contain the credentials for authenticating
            // This token is used as input to the authentication process
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword());

            // Authenticating the Login Credentials
            authManager.authenticate(authInputToken);

            // If this point is reached it means Authentication was successful
            // Generate the JWT
            String token = jwtUtil.generateToken(body.getEmail());

            // Respond with the JWT
            return Collections.singletonMap("jwt-token", token);
        }catch (AuthenticationException authExc){
            // Auhentication Failed
            throw new RuntimeException("Invalid Login Credentials");
        }
    }

    // Defining the function to handle the GET route to fetch user information of the authenticated user
    @GetMapping("/infox")
    public User getUserDetailsx(){
        // Retrieve email from the Security Context
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Fetch and return user details
        return userRepo.findByEmail(email).get();
    }


}
