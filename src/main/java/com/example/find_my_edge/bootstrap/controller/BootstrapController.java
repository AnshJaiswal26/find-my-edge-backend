package com.example.find_my_edge.bootstrap.controller;

import com.example.find_my_edge.bootstrap.dto.BootstrapResponse;
import com.example.find_my_edge.bootstrap.service.BootstrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/app/init")
@RequiredArgsConstructor
public class BootstrapController {

    private final BootstrapService bootstrapService;

    @GetMapping
    public ResponseEntity<BootstrapResponse> initApp(){
        BootstrapResponse init = bootstrapService.init();
        return ResponseEntity.ok(init);
    }
}
