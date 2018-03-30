package edu.uab.registry.dao;

import edu.uab.registry.domain.GenericRegistryPatient;
import edu.uab.registry.exception.DaoException;

import java.util.List;

public interface SearchRegistryPatientDao 
{

    // This method supports "advanced search" functionality to retrieve registry patients.
    List<GenericRegistryPatient> search(String assignedBy_,
                                        String assignmentDateFrom_,
                                        String assignmentDateTo_,
                                        String detectionEvents_,
                                        String encounterAttributes_,
                                        String encounterDateFrom_,
                                        String encounterDateTo_,
                                        String firstName_,
                                        String lastName_,
                                        String lastReviewFrom_,
                                        String lastReviewTo_,
                                        String mrn_,
                                        String patientAttributes_,
                                        Integer registryID_,
                                        String registryStatusID_,
                                        String reviewedBy_,
                                        String ssn_,
                                        String workflowStatusID_) throws DaoException;

    // Search for registry patients by MRN or by name (first and/or last).
    // TODO: this will likely need to be replaced by a faster and more lightweight implementation that supports autocomplete.
    List<GenericRegistryPatient> searchForMrnOrName(int registryID_, String searchText_) throws DaoException;

}
