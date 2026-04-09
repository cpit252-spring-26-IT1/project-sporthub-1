package sa.edu.kau.fcit.cpit252.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sa.edu.kau.fcit.cpit252.project.User;
import sa.edu.kau.fcit.cpit252.project.repositories.UserRepository;
import sa.edu.kau.fcit.cpit252.project.patterns.AuditManager;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("{\"message\": \"Email is already registered!\"}");
        }
        
        userRepository.save(user);
        AuditManager.getInstance().logAction(user.getEmail(), "Registered a new account");
        
        return ResponseEntity.ok("{\"message\": \"Account created successfully!\"}");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());
        
        if(user.isPresent() && user.get().getPassword().equals(loginRequest.getPassword())) {
            AuditManager.getInstance().logAction(user.get().getEmail(), "Logged in successfully");
            return ResponseEntity.ok("{\"message\": \"success\", \"name\": \"" + user.get().getName() + "\"}");
        }
        
        AuditManager.getInstance().logAction(loginRequest.getEmail(), "Failed login attempt");
        return ResponseEntity.status(401).body("{\"message\": \"Invalid email or password!\"}");
    }
}