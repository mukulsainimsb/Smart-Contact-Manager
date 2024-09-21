package com.smartcontact.project.smart_contact_manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.smartcontact.project.smart_contact_manager.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact,Integer> {

	
	//retrieving all the contacts of a particular user
	public List<Contact> findByUserId(int userId);
}
