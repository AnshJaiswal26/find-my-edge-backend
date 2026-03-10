package com.example.find_my_edge.dev.controller;


import com.example.find_my_edge.common.auth.entity.User;
import com.example.find_my_edge.common.auth.repository.UserRepository;
import com.example.find_my_edge.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevController {


    private final WorkspaceService workspaceService;
    private final UserRepository userRepository;


    @PostMapping("/init/{userId}")
    public ResponseEntity<String> reInitWorkspace(@PathVariable String userId) {


        return ResponseEntity.ok("Created");
    }

    @PostMapping("/user/{email}")
    public ResponseEntity<String> getUserByEmail(@PathVariable String email) {
        Optional<User> byEmail =
                userRepository.findByEmail(email);

        if(byEmail.isEmpty()){
            return ResponseEntity.ok("User not found");
        }

        UUID id = byEmail.get().getId();

        workspaceService.deleteByUserId(id);
        workspaceService.createDefaultWorkspace(id);

        return ResponseEntity.ok("Workspace recreated");
    }
}
