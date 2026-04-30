package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.MainService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.project.back_end.utils.AppHelper.composeResponse;
import static com.project.back_end.utils.TimePeriodOfDay.mapToTimePeriod;

@RestController
@RequestMapping("${api.path}" + "v1/doctor")
public class DoctorController {
    private final DoctorService doctorService;
    private final MainService mainService;
    
    @Autowired
    public DoctorController(DoctorService doctorService, MainService mainService) {
        this.doctorService = doctorService;
        this.mainService = mainService;
    }
    
    /**
     * Handles HTTP POST requests for doctor login.
     * * Accepts a validated `Login` DTO containing credentials.
     * * Delegates authentication to the `DoctorService` and returns login status and token information.
     * @param loginDTO username / email, password
     * @return returns login status and token information.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody @Valid Login loginDTO) {
        return composeResponse(HttpStatus.OK, "token",
            mainService.validateDoctorLogin(loginDTO.getIdentifier(), loginDTO.getPassword()));
    }
    
    /**
     * Handles HTTP POST requests to save a new doctor.<p>
     * * Accepts a validated `Doctor` object in the request body and a token for authorization.<br>
     * * Validates the token for the `"admin"` role before proceeding.</p>
     * @param token JWT token for the `"admin"` role
     * @param doctor to save as new doctor
     * @return If the doctor already exists, returns a conflict response; otherwise,
     *          adds the doctor and returns a success message.
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> registerDoctor(
        @PathVariable("Authorization") @Valid String token,
        @RequestBody @Valid Doctor doctor) {
        
        mainService.validateToken(token, "admin");
        doctorService.createDoctor(doctor);
        return composeResponse(HttpStatus.CREATED, "message", "Doctor successfully registered");
    }
    
    /**
     * Handles HTTP GET requests to check a specific doctor’s availability on a given date.<p>
     * * Requires `user` type, `doctorId`, `date`, and `token` as path variables.<br>
     * * First validates the token against the user type.</p>
     * @param user `user` type
     * @param token and `token`
     * @param id `doctor's ID as long
     * @param date `date`
     * @return If the token is invalid, returns an error response; otherwise, returns the availability status for the doctor.
     */
    @GetMapping("/availability/{id}/{date}/{user}/{token}")
    public ResponseEntity<Map<String, List<String>>> getDoctorAvailability(
        @PathVariable("X-User") @Valid String user,
        @PathVariable("Authorization") @Valid String token,
        @PathVariable @Valid long id,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Valid LocalDate date) {
        
        mainService.validateToken(token, user);
        return composeResponse(HttpStatus.OK, "availableTimes", doctorService.getDoctorAvailability(id, date));
    }
    
    /**
     * Handles HTTP GET requests to retrieve a list of all doctors.<p>
     * * Returns the list within a response map under the key `"doctors"` with HTTP 200 OK status.</p>
     * @return HTTP 200 OK and body with doctor's list
     */
    @GetMapping("/getDoctors")
    public ResponseEntity<Map<String, List<Doctor>>> getDoctors() {
        return composeResponse(HttpStatus.OK, "doctors", doctorService.getDoctors());
    }
    
    /**
     * Handles HTTP GET requests to filter doctors based on name, time, and specialty.<p>
     * * Calls the shared `MainService` to perform filtering logic and returns matching doctors in the response.</p>
     * @param name contained partially in doctor's name.
     * @param time for appointment availability.
     * @param specialty of doctors.
     * @return ResponseEntity<Map<String, List<Doctor>>>
     */
    @GetMapping("/filer/{name}/{time}/{specialty}")
    public ResponseEntity<Map<String, List<Doctor>>> filterDoctors(
        @PathVariable @Valid String name,
        @PathVariable @Valid String time,
        @PathVariable @Valid String specialty) {
        
        return composeResponse(HttpStatus.OK, "doctors", mainService.filterDoctor(name, specialty, mapToTimePeriod(time)));
    }
    
    /**
     * Handles HTTP PUT requests to update an existing doctor's information.<p>
     * * Accepts a validated `Doctor` object and a token for authorization.<br>
     * * Token must belong to an `"admin"`.<br>
     * * If the doctor exists, updates the record and returns success; otherwise,
     *   returns not found or error messages.</p>
     * @param token JWT token
     * @param doctor fulfilled with the data to be updated
     * @return ResponseEntity<Map<String, String>>
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
        @PathVariable("Authorization") @Valid String token,
        @RequestBody @Valid Doctor doctor) {
        
        mainService.validateToken(token, "admin");
        doctorService.updateDoctor(doctor);
        return composeResponse(HttpStatus.OK, "message", "Doctor successfully updated");
    }
    
    /**
     * Handles HTTP DELETE requests to remove a doctor by ID.<p>
     * * Requires both doctor ID and an admin token as path variables.<br>
     * * If the doctor exists, deletes the record and returns a success message; otherwise,
     *   responds with a not found or error message.</p>
     * @param token JWT OAuth2
     * @param id d
     * @return ResponseEntity<Map<String, String>>
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
        @PathVariable("Authorization") @Valid String token,
        @PathVariable("id") @Valid long id) {
        
        mainService.validateToken(token, "admin");
        doctorService.deleteDoctor(id);
        return composeResponse(HttpStatus.OK, "message",
            "Doctor and its associated appointments successfully deleted");
    }
    

}
