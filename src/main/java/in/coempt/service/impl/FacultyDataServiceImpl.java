package in.coempt.service.impl;


import in.coempt.entity.FacultyData;
import in.coempt.repository.FacultyDataRepository;
import in.coempt.service.FacultyDataService;
import in.coempt.util.CSVHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class FacultyDataServiceImpl implements FacultyDataService {

    @Autowired
    private FacultyDataRepository facultyDataRepository;

    public List<FacultyData> getAllFaculties() {
        return facultyDataRepository.findAll();
    }

    public Optional<FacultyData> getFacultyById(Integer id) {
        return facultyDataRepository.findById(id);
    }

    public Optional<FacultyData> getFacultyByMobileNumber(String mobileNumber) {
        return facultyDataRepository.findByMobileNumber(mobileNumber);
    }

    public FacultyData saveFaculty(FacultyData facultyData) {
        return facultyDataRepository.save(facultyData);
    }

    public void deleteFaculty(Integer id) {
        facultyDataRepository.deleteById(id);
    }
    public void saveCSV(MultipartFile file) {
        try {
            List<FacultyData> facultyList = CSVHelper.csvToFacultyData(file.getInputStream());

            for (FacultyData faculty : facultyList) {
                // Check if a record exists by mobile number
                Optional<FacultyData> existingFacultyOpt = facultyDataRepository.findByMobileNumber(faculty.getMobileNumber());

                if (existingFacultyOpt.isPresent()) {
                    FacultyData existingFaculty = existingFacultyOpt.get(); // Unwrap Optional

                    // Update existing record
                    existingFaculty.setFirstName(faculty.getFirstName());
                    existingFaculty.setLastName(faculty.getLastName());
                    existingFaculty.setEmail(faculty.getEmail());
                    existingFaculty.setCollegeCode(faculty.getCollegeCode());
                    existingFaculty.setTeachingExp(faculty.getTeachingExp());
                    existingFaculty.setIndustryExp(faculty.getIndustryExp());
                    existingFaculty.setDesignation(faculty.getDesignation());

                    facultyDataRepository.save(existingFaculty); // Save updated record
                } else {
                    // Save new record
                    facultyDataRepository.save(faculty);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to store CSV data: " + e.getMessage());
        }
    }


}

