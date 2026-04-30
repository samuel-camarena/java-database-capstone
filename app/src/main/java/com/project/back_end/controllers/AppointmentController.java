package com.project.back_end.controllers;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.DTO.DtoMapper;
import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.MainService;
import com.project.back_end.utils.outputhelpers.MessageFormatter.MsgHeader;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.project.back_end.utils.AppHelper.composeResponse;

/**
 * This class centralizes all routes that deal with booking, updating, retrieving, and canceling appointments.
 */
@RestController
@RequestMapping("/appointments")
public class AppointmentController {
    
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentService appointmentService;
    private final MainService mainService;
    private final DtoMapper dtoMapper;
    
    @Autowired
    public AppointmentController(AppointmentService appointmentService, MainService mainService, DtoMapper dtoMapper) {
        this.appointmentService = appointmentService;
        this.mainService = mainService;
        this.dtoMapper = dtoMapper;
    }
    
    /**
     * Handles HTTP POST requests to create a new appointment.<p>
     * * Accepts a validated `Appointment` object in the request body and a token as a path variable.<br>
     * * Validates the token for the `"patient"` role.<br>
     * * Uses service logic to validate the appointment data (e.g., check for doctor availability and time conflicts).</p>
     * @param token token for the `"patient"` role.
     * @param appointDTO a new appointment with DTO format.
     * @return Success message if booked, or appropriate error messages if the doctor ID is invalid or the slot is already taken.
     */
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(
        @PathVariable("Authorization") @Valid String token,
        @RequestBody @Valid AppointmentDTO appointDTO) {
        
        mainService.validateToken(token, "patient");
        Appointment appoint = dtoMapper.mapDTOtoAppointment(appointDTO);
        
        appointmentService.bookAppointment(appoint);
        logger.info("{}bookAppointment:: {}", MsgHeader.SUCCESS.compose(), "Appointment booked with data: " + appointDTO);
        return composeResponse(HttpStatus.CREATED, "message", "Appointment successfully booked");
    }
    
    /**
     * Handles HTTP GET requests to fetch appointments based on date and patient name.<p>
     * * Takes the appointment date, patient name, and token as path variables.<br>
     * * First validates the token for role `"doctor"` using the `MainService`.</p>
     * @param token of type JWT for role `"doctor"`
     * @param date appointment day
     * @param patientName p
     * @return appointments for the given patient on the specified date, if token is valid. Otherwise,
     * throws InvalidJwtTokenException
     */
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<Map<String, List<AppointmentDTO>>> getAppointments(
        @PathVariable("Authorization") @Valid String token,
        @PathVariable("date") @Valid LocalDate date,
        @PathVariable("patientName") @Valid String patientName) {
        
        mainService.validateToken(token, "doctor");
        long doctorId = mainService.extractSubjectIdFromToken(token, "doctor");
        
        List<AppointmentDTO> appointsDTO = dtoMapper.mapAppointmentsToDTOs(
            appointmentService.getAppointments(doctorId, date, patientName));
        logger.info("{}getAppointments::", MsgHeader.SUCCESS.compose());
        return composeResponse(HttpStatus.OK, "appointments", appointsDTO);
    }
    
    /**
     * Handles HTTP PUT requests to modify an existing appointment.<p>
     * * Accepts a validated `Appointment` object and a token as input.<br>
     * * Validates the token for `"patient"` role.<br>
     * * Delegates the update logic to the `AppointmentService`.<br>
     * * Returns an appropriate success or failure response based on the update result</p>
     * @param token token for `"patient"` role.
     * @param appointDTO to modify an existing appointment
     * @return Returns an appropriate success or failure response
     */
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(
        @PathVariable("Authorization") @Valid String token,
        @RequestBody @Valid AppointmentDTO appointDTO) {
        
        mainService.validateToken(token, "patient");
        Appointment appoint = dtoMapper.mapDTOtoAppointment(appointDTO);
        
        appointmentService.updateAppointment(appoint);
        logger.info("{}updateAppointment::", MsgHeader.SUCCESS.compose());
        return composeResponse(HttpStatus.OK, "message", "Appointment successfully updated");
    }
    
    /**
     * Handles HTTP DELETE requests to cancel a specific appointment.
     * * Accepts the appointment ID and a token as path variables.
     * * Validates the token for `"patient"` role to ensure the user is authorized to cancel the appointment.
     * * Calls `AppointmentService` to handle the cancellation process and returns the result.
     * @param token String token for `"patient"` role
     * @param id long appointment ID
     * @return returns HTTP OK 200, and descriptive message.
     */
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(
        @PathVariable("Authorization") @Valid String token,
        @PathVariable("id") @Valid long id) {
        
        mainService.validateToken(token, "patient");
        appointmentService.cancelAppointment(id, token);
        logger.info("{}cancelAppointment:: {}", MsgHeader.SUCCESS.compose(), "Appointment canceled by ID: "
            + id + " and patient ID: " + mainService.extractSubjectIdFromToken(token, "patient"));
        return composeResponse(HttpStatus.OK, "message", "Appointment successfully canceled");
    }
}
