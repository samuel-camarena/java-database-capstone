package com.project.back_end.DTO;

import com.project.back_end.exceptions.ResourceNotFoundException;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.utils.outputhelpers.MessageFormatter;
import com.project.back_end.utils.outputhelpers.MessageFormatter.MsgHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DtoMapper {
    private static final Logger logger = LoggerFactory.getLogger(DtoMapper.class);
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;
    
    public DtoMapper(DoctorRepository doctorRepo, PatientRepository patientRepo) {
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
    }
    
    public Appointment mapDTOtoAppointment(AppointmentDTO dto) {
        if (dto == null) return null;
        
        Doctor doctor = doctorRepo
            .findById(dto.getDoctorId())
            .orElseThrow(() -> {
                logger.warn("{}mapDTOtoAppointment:: {}", MessageFormatter.MsgHeader.FAIL.compose(),
                    "Doctor not found by ID: " + dto.getDoctorId());
                return new ResourceNotFoundException("Doctor not found by ID: " + dto.getDoctorId());
            });
        
        Patient patient = patientRepo
            .findById(dto.getPatientId())
            .orElseThrow(() -> {
                logger.warn("{}mapDTOtoAppointment:: {}", MsgHeader.FAIL.compose(),
                    "Patient not found by ID: " + dto.getPatientId());
                return new ResourceNotFoundException("Patient not found by ID: " + dto.getPatientId());
            });
        
        return new Appointment(
            doctor,
            patient,
            dto.getAppointmentDateTime(),
            dto.getStatus(),
            dto.getReasonForVisiting(),
            dto.getNotes());
    }
    
    public AppointmentDTO mapAppointmentToDTO(Appointment appoint) {
        if (appoint == null) return null;
        
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
    
    public List<AppointmentDTO> mapAppointmentsToDTOs(List<Appointment> appoints) {
        if (appoints.isEmpty()) return List.of();
        
        return appoints
            .stream()
            .map(this::mapAppointmentToDTO)
            .toList();
    }
    
    public List<Appointment> mapDTOsToAppointments(List<AppointmentDTO> dtos) {
        if (dtos.isEmpty()) return List.of();
        
        return dtos
            .stream()
            .map(this::mapDTOtoAppointment)
            .toList();
    }
    
    public DoctorDTO mapDoctorToDTO(Doctor doctor) {
        if (doctor == null) return null;
        
        return new DoctorDTO(
            (doctor.getId() > 0) ? doctor.getId() : 0,
            doctor.getName(),
            doctor.getEmail(),
            doctor.getPassword(),
            doctor.getSpecialty(),
            doctor.getPhone(),
            doctor.getAvailableTimes(),
            doctor.getYearsOfExperience(),
            doctor.getClinicAddress(),
            doctor.getRating());
    }
    
    public Doctor mapDTOtoDoctor(DoctorDTO dto) {
        if (dto == null) return null;
        
        return new Doctor.Builder()
            .id(dto.getId() > 0 ? dto.getId() : 0)
            .name(dto.getName())
            .specialty(dto.getSpecialty())
            .password(dto.getPassword())
            .email(dto.getEmail())
            .phone(dto.getPhone())
            .availableTimes(dto.getAvailableTimes())
            .clinicAddress(dto.getClinicAddress())
            .yearsOfExperience(dto.getYearsOfExperience())
            .rating(dto.getRating())
            .build();
    }
}
