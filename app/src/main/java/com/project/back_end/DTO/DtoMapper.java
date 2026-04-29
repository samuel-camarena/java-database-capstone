package com.project.back_end.DTO;

import com.project.back_end.exceptions.ResourceNotFoundException;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.utils.outputhelpers.MessageFormatter.MessageHead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {
    private static final Logger logger = LoggerFactory.getLogger(DtoMapper.class);
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;
    
    public DtoMapper(DoctorRepository doctorRepo, PatientRepository patientRepo) {
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
    }
    
    public Appointment mapDTOtoAppointment(AppointmentDTO appointDTO) {
        if (appointDTO == null) return null;
        
        Doctor doctor = doctorRepo
            .findById(appointDTO.getDoctorId())
            .orElseThrow(() -> {
                logger.warn("{}mapDTOtoAppointment:: {}", MessageHead.FAIL.compose(),
                    "Doctor not found by ID: " + appointDTO.getDoctorId());
                return new ResourceNotFoundException("Doctor not found by ID: " + appointDTO.getDoctorId());
            });
        
        Patient patient = patientRepo
            .findById(appointDTO.getPatientId())
            .orElseThrow(() -> {
                logger.warn("{}mapDTOtoAppointment:: {}", MessageHead.FAIL.compose(),
                    "Patient not found by ID: " + appointDTO.getPatientId());
                return new ResourceNotFoundException("Patient not found by ID: " + appointDTO.getPatientId());
            });
        
        return new Appointment(
            doctor,
            patient,
            appointDTO.getAppointmentDateTime(),
            appointDTO.getStatus(),
            appointDTO.getReasonForVisiting(),
            appointDTO.getNotes());
    }
    
    public AppointmentDTO mapAppointmentToDTO(Appointment appoint) {
        return new AppointmentDTO(
            appoint.getId(),
            appoint.getDoctor().getId(),
            appoint.getDoctor().getName(),
            appoint.getPatient().getId(),
            appoint.getPatient().getName(),
            appoint.getPatient().getEmail(),
            appoint.getPatient().getPhone(),
            appoint.getPatient().getAddress(),
            appoint.getStatus(),
            appoint.getAppointmentTime(),
            appoint.getReasonForVisiting(),
            (appoint.getNotes().isBlank()) ? "" : appoint.getNotes());
    }
}
