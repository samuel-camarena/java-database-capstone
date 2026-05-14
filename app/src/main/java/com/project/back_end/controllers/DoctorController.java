package com.project.back_end.controllers;

import com.project.back_end.DTO.DtoMapper;
import com.project.back_end.DTO.LoginDTO;
import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.MainService;
import com.project.back_end.utils.TimePeriodOfDay;
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
import static com.project.back_end.utils.TimePeriodOfDay.mapStringAmOrPmToTimePeriod;

@RestController
@RequestMapping("${api.path}" + "v1/doctor")
public class DoctorController {
    private final DoctorService doctorService;
    private final MainService mainService;
    private final DtoMapper dtoMapper;
    
    @Autowired
    public DoctorController(DoctorService doctorService, MainService mainService,  DtoMapper dtoMapper) {
        this.doctorService = doctorService;
        this.mainService = mainService;
        this.dtoMapper = dtoMapper;
    }
    
    /**
     * Handles HTTP POST requests for doctor login.
     * * Accepts a validated `LoginDTO` DTO containing credentials.
     * * Delegates authentication to the `DoctorService` and returns login status and token information.
     * @param loginDto username / email, password
     * @return returns login status and token information.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody @Valid LoginDTO loginDto) {
        return composeResponse(HttpStatus.OK, "token",
            mainService.validateDoctorLogin(loginDto.getIdentifier(), loginDto.getPassword()));
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
    public ResponseEntity<Map<String, String>> createDoctor(
        @PathVariable @Valid String token,
        @RequestBody @Valid Doctor doctor) {
        
        mainService.validateToken(token, "admin");
        //Doctor doc = dtoMapper.mapDTOtoDoctor(doctorDto);
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
        //@PathVaria
        // ble("X-User") @Valid String user,
        @PathVariable @Valid String user,
        @PathVariable @Valid String token,
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
    @GetMapping("")
    public ResponseEntity<Map<String, List<Doctor>>> getAllDoctors() {
        return composeResponse(HttpStatus.OK, "doctors", doctorService.getAllDoctors());
    }
    
    /**
     * Handles HTTP GET requests to filter doctors based on name, time, and specialty.<p>
     * * Calls the shared `MainService` to perform filtering logic and returns matching doctors in the response.</p>
     * @param name contained partially in doctor's name.
     * @param amOrPm for appointment availability.
     * @param specialty of doctors.
     * @return ResponseEntity<Map<String, List<Doctor>>>
     */
    @GetMapping("/filter")
    public ResponseEntity<Map<String, List<Doctor>>> filterAllDoctorsV2(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String amOrPm,
        @RequestParam(required = false) String specialty) {
        
        name = sanitizeStringParam(name);
        specialty = sanitizeStringParam(specialty);
        TimePeriodOfDay period = sanitizeStringTimePeriodOfDay(amOrPm);
        
        return composeResponse(HttpStatus.OK, "doctors", filterDoctors(name, specialty, period));
    }
    
    @GetMapping("/filter/{name}/{time}/{specialty}/{token}")
    public ResponseEntity<Map<String, List<Doctor>>> filterAllDoctors(
        @PathVariable String name,
        @PathVariable String time,
        @PathVariable String specialty) {
        
        name = sanitizeStringParam(name);
        specialty = sanitizeStringParam(specialty);
        TimePeriodOfDay period = sanitizeStringTimePeriodOfDay(time);
        
        return composeResponse(HttpStatus.OK, "doctors", filterDoctors(name, specialty, period));
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
        //@PathVariable("Authorization") @Valid String token,
        @PathVariable @Valid String token,
        @RequestBody @Valid Doctor doctor) {
        
        mainService.validateToken(token, "admin");
        //Doctor doc = dtoMapper.mapDTOtoDoctor(doctorDto);
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
        @PathVariable("token") @Valid String token,
        @PathVariable("id") @Valid long id) {
        
        mainService.validateToken(token, "admin");
        doctorService.deleteDoctor(id);
        return composeResponse(HttpStatus.OK, "message",
            "Doctor and its associated appointments successfully deleted");
    }
    
    /**
     * This method provides optional filtering functionality for doctors based on name, specialty, and available time slots.<br>
     * This flexible filtering mechanism allows the frontend or consumers of the API to search and
     * narrow down doctors based on user criteria by any of the three filters.<p>
     * * If none of the filters are provided, it returns all available doctors.</p>
     * @param name doctor's name
     * @param specialty doctor's specialty
     * @param period TimePeriodOfDay
     * @return List of doctors
     */
    private List<Doctor> filterDoctors(String name, String specialty, TimePeriodOfDay period) {
        if (name.isBlank()) {
            if (specialty.isBlank()) {
                if (period == null) {
                    return doctorService.getAllDoctors();
                }
                return doctorService.filterAllDoctorsByTimePeriod(period);
            } else {
                if (period == null) {
                    return doctorService.filterDoctorsBySpecialty(specialty);
                }
                return doctorService.filterDoctorsByTimePeriodAndSpecialty(specialty, period);
            }
        } else {
            if (specialty.isBlank()) {
                if (period == null) {
                    return doctorService.findDoctorsByName(name);
                }
                return doctorService.filterDoctorsByNameAndTimePeriod(name, period);
            } else {
                if (period == null) {
                    return doctorService.filterDoctorsByNameAndSpecialty(name, specialty);
                }
                return doctorService.filterDoctorsByNameAndSpecialtyAndTimePeriod(name, specialty, period);
            }
        }
    }
    
    private String sanitizeStringParam(String param) {
        if (param == null || param.isBlank() || param.equalsIgnoreCase("null")) {
            return "";
        }
        return param;
    }

    private TimePeriodOfDay sanitizeStringTimePeriodOfDay(String period) {
        return mapStringAmOrPmToTimePeriod(period);
    }
}
