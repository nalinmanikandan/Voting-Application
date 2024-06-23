package com.voting.voteapp.repository;

import com.voting.voteapp.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Admin findById(int id);
    boolean existsByUsername(String name);
    Admin findByUsername(String name);

}
