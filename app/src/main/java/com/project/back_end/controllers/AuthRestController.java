package com.project.back_end.controllers;

import com.project.back_end.DTO.LoginDTO;
import com.project.back_end.services.MainService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.project.back_end.utils.AppHelper.composeResponse;

@RestController
@RequestMapping("${api.path}" + "v1/auth")
public class AuthRestController {
    
    private final MainService mainService;
    
    @Autowired
    public AuthRestController(MainService mainService) {
        this.mainService = mainService;
    }
    
    /**
     * Handles HTTP POST requests for any user role login.<p>
     * * Accepts an `LoginDTO` object in the request body, which contains role and login credentials.<br>
     * * Delegates authentication to the `validateUserLogin` method in the shared service.</p>
     * @return Returns a response with a token or an error message depending on login success.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> userLogin(@RequestBody @Valid LoginDTO loginDto) {
        return composeResponse(HttpStatus.OK, "token",
            mainService.validateUserLogin(loginDto.getIdentifier(), loginDto.getPassword(), loginDto.getRole()));
    }
}
