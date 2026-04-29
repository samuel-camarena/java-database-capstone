
package com.project.back_end.controllers;

import com.project.back_end.models.Admin;
import com.project.back_end.services.MainService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.project.back_end.utils.AppHelper.composeResponse;

@RestController
@RequestMapping("${api.path}" + "v1/admin")
public class AdminController {
    private final MainService mainService;
    
    @Autowired
    public AdminController(MainService mainService) {
        this.mainService = mainService;
    }

    /**
     * Handles HTTP POST requests for admin login.<p>
     * * Accepts an `Admin` object in the request body, which contains login credentials.<br>
     * * Delegates authentication to the `validatePatientLogin` method in the shared service.</p>
     * @return Returns a response with a token or an error message depending on login success.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody @Valid Admin admin) {
        return composeResponse(HttpStatus.OK, "token",
            mainService.validateAdminLogin(admin.getUsername(), admin.getPassword()));
    }
}

