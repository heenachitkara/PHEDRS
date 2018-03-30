package edu.uab.registry.dao;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.uab.registry.domain.RegistryPatientStatus;
import edu.uab.registry.exception.DaoException;
import edu.uab.registry.orm.RegistryPatient;
import edu.uab.registry.util.Constants.RegistryStatusType;

public interface RegistryPatientStatusDao {

    List<RegistryPatientStatus> getStatusHistory(RegistryPatient registryPatient_,
												 RegistryStatusType statusType_) throws DaoException;
	
	void updateStatus(ClassPathXmlApplicationContext context_,
					  Integer registrarID_,
					  RegistryPatient registryPatient_,
                      String registryStatusComment_,
					  Integer registryStatusID_,
					  String workflowStatusComment_,
					  Integer workflowStatusID_) throws DaoException;
	
}
