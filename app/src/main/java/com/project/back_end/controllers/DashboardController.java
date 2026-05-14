package com.project.back_end.controllers;

import com.project.back_end.exceptions.BusinessLogicException;
import com.project.back_end.exceptions.DatabaseAccessException;
import com.project.back_end.exceptions.InvalidJwtTokenException;
import com.project.back_end.services.MainService;
import com.project.back_end.utils.outputhelpers.MessageFormatter.MsgHeader;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * This class handles routing to admin and doctor dashboard pages based on token validation.
 */
@Controller
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private final MainService mainService;
    
    @Autowired
    public DashboardController(MainService mainService) {
        this.mainService = mainService;
    }
    
    /**
     * This method handles HTTP GET requests to `/adminDashboard/{token}`.<p>
     * * Accepts an admin's token as a path variable.<br>
     * * Validates the token using the shared service for the `"admin"` role.</p>
     * @param token JWT Authentication token
     * @return If the token is valid, forwards the user to the `"admin/adminDashboard"` view. Otherwise,
     * then redirects to the root URL, likely the login or home page.
     */
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable @Valid String token) {
        mainService.validateToken(token, "admin");
        return "dashboards/adminDashboard"; // "redirect:/admin/adminDashboard";
    }
    
    /**
     * This method handles HTTP GET requests to `/doctorDashboard/{token}`.<p>
     * * Accepts a doctor's token as a path variable.<br>
     * * Validates the token using the shared service for the `"doctor"` role.</p>
     * @param token JWT Authentication token.
     * @return If the token is valid, forwards the user to the `"doctor/doctorDashboard"` view. Otherwise,
     * then redirects to the root URL.
     */
    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable("token") String token) {
        mainService.validateToken(token, "doctor");
        return "dashboards/doctorDashboard"; // "redirect:/doctor/doctorDashboard";
    }
    
    @GetMapping("/patientDashboard/{token}")
    public String patientDashboard() {
        //mainService.validateToken(token, "doctor");
        return "dashboards/patientDashboard"; // "redirect:/doctor/doctorDashboard";
    }
    
    
    @ExceptionHandler(InvalidJwtTokenException.class)
    private String handleInvalidJwtTokenException(InvalidJwtTokenException Ex) {
        logger.error("{}handleInvalidJwtTokenException:: ({}) -> Exception msg: ({})", MsgHeader.ERROR.compose(),
            "Redirecting to URL:/", Ex.getMessage());
        return "redirect:/";
    }
}
