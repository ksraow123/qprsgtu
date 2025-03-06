package in.coempt.service.impl;

import in.coempt.entity.*;
import in.coempt.repository.SetterModeratorRepository;
import in.coempt.repository.UserDataRepository;
import in.coempt.service.*;
import in.coempt.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CSVServiceImpl implements CSVService {

    private final SubjectsService subjectsService;
        private final UserDataRepository userDataRepository;
    private final BCryptPasswordEncoder passwordEncoder;
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final CollegeService collegeService;
    private final UserService userService;
    private final AppointmentService appointmentService;
    private final SetterModeratorRepository moderatorRepository;
        public void saveCSV(MultipartFile file) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                List<UserData> userList = new ArrayList<>();
                String line;
                boolean firstLine = true; // Skip header

                while ((line = reader.readLine()) != null) {
                    if (firstLine) {
                        firstLine = false;
                        continue;
                    }

                    String[] data = line.split(",");

                    if (data.length == 10) {
                        UserData user = getUserData(data);
                        userList.add(user);
                    }
                }
                userDataRepository.saveAll(userList);
            } catch (Exception e) {
                throw new RuntimeException("Failed to process CSV file: " + e.getMessage());
            }
        }

    @Override
    public void saveMappingCSV(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            List<SetterModeratorMapping> userList = new ArrayList<>();
            String line;
            boolean firstLine = true; // Skip header

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] data = line.split(",");

                if (data.length == 3) {
                    SetterModeratorMapping user = getSetterModeratorMapping(data);
                    userList.add(user);
                }
            }
            moderatorRepository.saveAll(userList);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process CSV file: " + e.getMessage());
        }
    }

    private SetterModeratorMapping getSetterModeratorMapping(String[] data) {
        UserDetails userData=(UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User user = userService.getUserByUserName(data[0].trim());
        User user1 = userService.getUserByUserName(data[1].trim());
        Subjects subjects = subjectsService.getSubject_code(data[2].trim());
        SetterModeratorMapping setterModeratorMapping=new SetterModeratorMapping();

        setterModeratorMapping.setSetterId(user.getId());
        setterModeratorMapping.setModeratorId(user1.getId());
        setterModeratorMapping.setAssigned_date(LocalDate.now().toString());
        setterModeratorMapping.setAssigned_by(userData.getUsername());
        setterModeratorMapping.setSubjectId(subjects.getId());
        return setterModeratorMapping;

    }

    private UserData getUserData(String[] data) {
        String customPassword = RandomUtils.nextLong(10000, 99999) + "";
        Subjects subjects = subjectsService.getSubject_code(data[2].trim());
        User user = userService.getUserByMobileNo(data[3].trim());
        if (user == null){
            user=new User();
          }
        user.setFirstName(data[0].trim());
        user.setLastName(data[1].trim());
        user.setIsActive(0);
        user.setMobileNo(data[3].trim());
        user.setEmail(data[4].trim());
        int roleId = data[9].trim().equalsIgnoreCase("S") ? 2 : 3;
        user.setUserName(generateUserName(roleId));
        user.setRoleId(roleId);
        user.setPassword(passwordEncoder.encode(customPassword));
        userService.saveUser(user);
        UserData userData=new UserData();
        userData.setNo_of_sets(Integer.parseInt(data[7].trim()));
        userData.setUser_id(Math.toIntExact(user.getId()));

        CollegeEntity collegeEntity=collegeService.getCollegeByCode(data[8].trim());
        userData.setCollege_id(String.valueOf(collegeEntity.getId()));
        userData.setOffice_order_date(data[5].trim());
        userData.setLast_date_to_submit(data[6].trim());
        userData.setNo_of_sets(Integer.parseInt(data[7].trim()));
        userData.setSubject_id(Math.toIntExact(subjects.getId()));

        userData.setRole_id(roleId);


       return userData;

    }

    private String generateUserName(int roleId) {
        return (roleId == 2 ? "S" : "M") + RandomUtils.nextLong(10000, 99999);
    }
}
