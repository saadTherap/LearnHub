package net.therap.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.therap.app.constants.CacheConstants;
import net.therap.app.dto.InstructorDTO;
import net.therap.app.dto.InstructorDtoCatalog;
import net.therap.app.helper.DtoHelper;
import net.therap.app.mapper.InstructorMapper;
import net.therap.app.model.Instructor;
import net.therap.app.model.enums.AuthorizationLevel;
import net.therap.app.service.AuthorizationService;
import net.therap.app.service.InstructorService;
import net.therap.app.validation.OnCreate;
import net.therap.app.validation.OnUpdate;
import net.therap.cache.support.HazelcastCacheService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author gazizafor
 * @since 21/7/25
 */
@Slf4j
@RestController
@RequestMapping("/instructors")
public class InstructorController {
    
    private final InstructorService instructorService;
    private final DtoHelper dtoHelper;
    private final InstructorMapper instructorMapper;
    private final HazelcastCacheService hazelcastCacheService;
    private final AuthorizationService authorizationService;
    private final MessageSource messageSource;
    
    @Autowired
    public InstructorController(InstructorService instructorService, DtoHelper dtoHelper, ObjectMapper objectMapper, InstructorMapper instructorMapper, HazelcastCacheService hazelcastCacheService, AuthorizationService authorizationService, MessageSource messageSource) { // Constructor injection
        this.instructorService = instructorService;
        this.dtoHelper = dtoHelper;
        this.instructorMapper = instructorMapper;
        this.hazelcastCacheService = hazelcastCacheService;
        this.authorizationService = authorizationService;
        this.messageSource = messageSource;
    }
    
    @GetMapping("/myProfile")
    public ResponseEntity<InstructorDTO> getMyProfile(HttpServletRequest request) throws BadRequestException {
        log.info("[GET] /instructors/myProfile");
        long id = authorizationService.getInstructorIdFromRequest(request);
        Optional<Instructor> instructorOptional = instructorService.getInstructorById(id);
        
        if (instructorOptional.isEmpty()) {
            throw new NoSuchElementException(messageSource.getMessage("instructor.not.found", null, request.getLocale()));
        }
        
        authorizationService.authorize(AuthorizationLevel.OWNER, instructorOptional.get(), request);
        
        return ResponseEntity.ok(instructorMapper.toInstructorDTO(instructorOptional.get()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<InstructorDTO> getInstructorById(@PathVariable long id, HttpServletRequest request) throws BadRequestException {
        log.info("GET /instructors/{}", id);
        InstructorDTO cached = hazelcastCacheService.get(CacheConstants.INSTRUCTORS, id);
        
        if (cached != null) {
            authorizationService.authorize(AuthorizationLevel.OWNER, cached, request);

            return ResponseEntity.ok(cached);

        } else {
            Optional<Instructor> instructorOptional = instructorService.getInstructorById(id);
            
            if (instructorOptional.isPresent()) {
                Instructor instructor = instructorOptional.get();
                
                authorizationService.authorize(AuthorizationLevel.OWNER, instructor, request);
                InstructorDTO dto = dtoHelper.toInstructorDTO(instructor);
                hazelcastCacheService.put(CacheConstants.INSTRUCTORS, id, dto);
                
                return ResponseEntity.ok(dto);
                
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }
    
    @GetMapping
    public ResponseEntity<List<InstructorDTO>> getAllInstructors(HttpServletRequest request) throws BadRequestException {
        log.info("[GET] /instructors");
        authorizationService.authorize(AuthorizationLevel.STUDENT, null, request);
        
        List<Instructor> instructors = instructorService.getAllInstructors();
        List<InstructorDTO> instructorDTOs = instructors.stream()
                .map(dtoHelper::toInstructorDTO) 
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(instructorDTOs);
    }
    
    @GetMapping("/byEmail/{email}")
    public ResponseEntity<InstructorDtoCatalog> getInstructorByEmail(@PathVariable String email, HttpServletRequest request) throws BadRequestException {
        log.info("[GET] /instructors/byEmail/{}", email);
        Optional<Instructor> instructorOptional = instructorService.getByEmailNonDeleted(email);
        
        if (instructorOptional.isPresent()) {
            authorizationService.authorize(AuthorizationLevel.OWNER, instructorOptional.get(), request);
            return ResponseEntity.ok(instructorMapper.toInstructorDtoCatalog(instructorOptional.get()));
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.instructor", null, Locale.getDefault()));
    }
    
    @PostMapping
    public ResponseEntity<InstructorDTO> createInstructor(@RequestBody @Validated(OnCreate.class) InstructorDTO instructorDTO, HttpServletRequest request) throws BadRequestException {
        log.info("[POST] /instructors. Req Body: {}", instructorDTO);
        authorizationService.authorize(AuthorizationLevel.ADMIN, new Instructor(), request);
        
        Instructor instructorToCreate = instructorMapper.toInstructor(instructorDTO);
        instructorToCreate.setId(0);
        Instructor createdInstructor = instructorService.createInstructor(instructorToCreate);
        
        return new ResponseEntity<>(dtoHelper.toInstructorDTO(createdInstructor), HttpStatus.CREATED);
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<InstructorDTO> updateInstructor(@PathVariable long id, @RequestBody @Validated(OnUpdate.class) InstructorDTO instructorDTO, HttpServletRequest request) throws BadRequestException {
        log.info("[PATCH] /instructors/{}. Req Body: {}", id, instructorDTO);
        Optional<Instructor> instructorToUpdate = instructorService.getInstructorById(id);
        
        if (instructorToUpdate.isPresent()) {
            authorizationService.authorize(AuthorizationLevel.OWNER, instructorToUpdate.get(), request);
            
            if (instructorDTO.getId() != id && instructorDTO.getId() != 0) {
                return ResponseEntity.badRequest().build();
            }
            
            instructorDTO.setId(instructorToUpdate.get().getId());
            instructorMapper.updateInstructorFromDto(instructorDTO, instructorToUpdate.get());
            Instructor updatedInstructor = instructorService.updateInstructor(instructorToUpdate.get());
            
            return new ResponseEntity<>(dtoHelper.toInstructorDTO(updatedInstructor), HttpStatus.OK);
            
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.instructor", null, Locale.getDefault()));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteInstructor(@PathVariable long id, HttpServletRequest request) throws BadRequestException {
        log.info("[DELETE] /instructors/{}", id);
        Optional<Instructor> instructorOptional = instructorService.getInstructorById(id);
        
        if (instructorOptional.isPresent()) {
            authorizationService.authorize(AuthorizationLevel.OWNER, instructorOptional.get(), request);
            instructorService.deleteById(id);
            
            return ResponseEntity.noContent().build();
        }
        
        throw new NoSuchElementException(messageSource.getMessage("not.found.instructor", null, Locale.getDefault()));
    }
}