package com.project.back_end.mvc;

import com.project.back_end.services.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * This class handles routing to admin and doctor dashboard pages based on token validation.
 */
@Controller
public class DashboardController {
    
    @Autowired
    private MainService mainService;
    
    @GetMapping("/adminDashboard/{token}")
    public String adminDashboard(@PathVariable("token") String token) {
//        try {
//            Map<String, String> response = MainService.isValidToken(token, "admin");
//            if(!response.isEmpty()) {
//                return "redirect:/"; // http://localhost:8080
//            }
            
            return "redirect:/admin/adminDashboard";
//        } catch (Exception e) {
//             System.err.println("Error :: DashboardController ::  adminDashboard ::" + e.getMessage());
//            return "redirect:/";
//        }
    }

    @GetMapping("/doctorDashboard/{token}")
    public String doctorDashboard(@PathVariable("token") String token) {
//        Map<String, String> response = MainService.isValidToken(token, "doctor");
//        if(!response.isEmpty()) {
//            return "redirect:/";
//        }
        
        return "redirect:/doctor/doctorDashboard";
    }
}
