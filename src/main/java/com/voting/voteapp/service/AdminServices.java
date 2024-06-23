package com.voting.voteapp.service;
import com.voting.voteapp.Dto.UserDto;
import com.voting.voteapp.Exceptions.*;
import com.voting.voteapp.entity.Admin;
import com.voting.voteapp.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServices {

    @Autowired
    public AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String ADMIN_USERNAME = "ECI-Admin36";

    public Admin signUp(UserDto admin){
        if(!ADMIN_USERNAME.equals(admin.getUsername())){
             throw new InvalidAdminUsernameException("Invalid Username");
        }

        if(adminRepository.existsByUsername(ADMIN_USERNAME)){
            throw new AdminAlreadyExistsException("Admin Already exists");
        }
        Admin newAdmin = new Admin();
        newAdmin.setUsername(admin.getUsername());
        String hashedPassword = passwordEncoder.encode(admin.getPassword());
        newAdmin.setPassword(hashedPassword);
        newAdmin.setElectionStatus(false);
        newAdmin.setElectionCompleted(false);
        return adminRepository.save(newAdmin);
    }

    public Admin signIn(UserDto userDto) {
        Optional<Admin> admin = Optional.ofNullable(adminRepository.findByUsername(userDto.getUsername()));
        /*Optional<String> admin = Optional.ofNullable(adminRepository.findByUsername(userDto.getUsername()));*/
        if (admin.isEmpty()) {
            throw new InvalidAdminUsernameException("Admin not found");
        }

        if (passwordEncoder.matches(userDto.getPassword(), admin.get().getPassword())) {
            return admin.get();
        } else {
            throw new InvalidAdminCredentialsException("Wrong Credentials");
        }
    }

    public void startElection(){
        Admin original = adminRepository.findAll().get(0);
        if(original.getElectionStatus()){
            throw new ElectionAlreadyStartedException("Election has Already Started");
        }
        original.setElectionStatus(true);
        original.setElectionCompleted(false);
        adminRepository.save(original);
    }

    public void stopElection(){
        Admin original = adminRepository.findAll().get(0);
        if(!original.getElectionStatus()){
            throw new ElectionAlreadyEndedException("Election has Already Closed");
        }
        original.setElectionStatus(false);
        original.setElectionCompleted(true);
        adminRepository.save(original);
    }

}
