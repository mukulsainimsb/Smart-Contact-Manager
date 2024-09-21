package com.smartcontact.project.smart_contact_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartcontact.project.smart_contact_manager.Userr;

@Repository
public interface UserRepository extends JpaRepository<Userr,Integer>{

	public Userr findByEmail(String username);

	public int findIdByName(String username);


}
