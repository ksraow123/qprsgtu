package in.coempt.controller;

import in.coempt.entity.*;
import in.coempt.service.*;
import in.coempt.util.SecurityUtil;
import in.coempt.util.SendMailUtil;
import in.coempt.vo.AppointmentVo;
import in.coempt.vo.IndividualAppointmentVo;
import in.coempt.vo.ProfileDetailsVo;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Value("${filePath}")
    private String filePath;


    @Autowired
    private TemplateEngine templateEngine;
    private final AppointmentService appointmentService;
    private final CourseService courseService;
    private final UserService userService;
    private final SendMailUtil sendMail;
    private final SubjectsService subjectsService;
    private final RolesService rolesService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SmsEmailTemplateService smsEmailTemplateService;
    private final CollegeService collegeService;
    private final UserDetailsService userDetailsService;


    @Value("${app.url}")
    private String appUrl;

    public AppointmentController(AppointmentService appointmentService, CourseService courseService,
                                 UserService userService, SendMailUtil sendMail, SubjectsService subjectsService,
                                 RolesService rolesService, BCryptPasswordEncoder passwordEncoder, SmsEmailTemplateService smsEmailTemplateService, CollegeService collegeService,UserDetailsService userDetailsService ) {
        this.appointmentService = appointmentService;
        this.courseService = courseService;
        this.userService = userService;
        this.sendMail = sendMail;
        this.subjectsService = subjectsService;
        this.rolesService = rolesService;
        this.passwordEncoder = passwordEncoder;
        this.smsEmailTemplateService = smsEmailTemplateService;
        this.collegeService = collegeService;
this.userDetailsService=userDetailsService;
    }

    @GetMapping("/list")
    public String getAppointmentList(Model model) {
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("appointmentList", appointmentService.getAllAppointments());
        model.addAttribute("coursesList", courseService.getAllCourses());
        model.addAttribute("collegeList", collegeService.getAllColleges());
        return "newappointment";
    }
    @GetMapping("/individualAppointments")
    public String individualAppointments(Model model) {
        model.addAttribute("coursesList", courseService.getAllCourses());
        model.addAttribute("collegeList", collegeService.getAllColleges());
        model.addAttribute("individualAppoint", new IndividualAppointmentVo());
        model.addAttribute("page","individualAppointment");
        return "main";
    }

    @PostMapping("/individual/save")
    @Transactional
    public String saveAppointment(Model model,IndividualAppointmentVo appointmentVo){
        String customPassword = RandomUtils.nextLong(10000, 99999) + "";

        User user = userService.getUserByMobileNo(appointmentVo.getMobile_number());
        if (user == null){
            user=new User();
        }
        user.setFirstName(appointmentVo.getFname());
        user.setLastName(appointmentVo.getLname());
        user.setIsActive(0);
        user.setMobileNo(appointmentVo.getMobile_number());
        user.setEmail(appointmentVo.getEmail());
        user.setUserName(generateUserNameByRole(appointmentVo.getRole_id()));
        user.setRoleId(appointmentVo.getRole_id());
        user.setPassword(passwordEncoder.encode(customPassword));
        userService.saveUser(user);
        UserData userData=new UserData();
        userData.setNo_of_sets(Integer.parseInt(appointmentVo.getNo_of_sets()));
        userData.setUser_id(Math.toIntExact(user.getId()));
        CollegeEntity collegeEntity=collegeService.getCollegeByCode(appointmentVo.getCollegeCode());
        userData.setCollege_id(String.valueOf(collegeEntity.getId()));
        userData.setOffice_order_date(appointmentVo.getOrder_date());
        userData.setLast_date_to_submit(appointmentVo.getSubmission_date());
        userData.setNo_of_sets(Integer.parseInt(appointmentVo.getNo_of_sets()));
        userData.setSubject_id(Math.toIntExact(appointmentVo.getSubject_id()));
        userData.setRole_id(appointmentVo.getRole_id());
      UserData usrData=  appointmentService.saveuserData(userData);
        Subjects subjects = subjectsService.getSubjectById(appointmentVo.getSubject_id()+"");
        Course course = courseService.getCourseDetailsById(subjects.getCourseId());

        LocalDate currentDate = LocalDate.now();
        // Define the desired date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Format the date to the desired format
        String formattedDate = currentDate.format(formatter);

        Context context = new Context();
        context.setVariable("date", formattedDate);
        context.setVariable("name", appointmentVo.getFname()+" "+appointmentVo.getLname());
        context.setVariable("collegeCode", collegeEntity.getCollegeCode());
        context.setVariable("subjectCode", subjects.getSubjectCode());
        context.setVariable("subjectName", subjects.getSubject_name());
        context.setVariable("yearCourse", subjects.getYear() + "/" + course.getCourse_name());
        context.setVariable("submissionDate", appointmentVo.getSubmission_date());

        String s1 = templateEngine.process("email-template", context);

        //  String emailBody =String.format(s1,formattedDate,appointment.getName(),appointment.getCollege_code(),
        //        appointment.getSubject_code(),subjects.getSubject_name(),   course.getCourse_name(),"02-03-2025");
        String syllabusFile=filePath+ File.separator+"1.pdf";
        String s2= s1 + "<p>Please click on the link below to accept your appointment :</p>" +
                "<p><a href='" + appUrl + "appointments/" + usrData.getId() + "/"+user.getUserName() +"/accept'>Accept Appointment</a></p>" +
                //   "<p><strong>Username:</strong> " + userName + "</p>" +
                // "<p><strong>Password:</strong> " + customPassword + "</p>" +
                "<p>If you are not willing to accept this appointment, please click the link below:</p>" +
                "<p><a href='" + appUrl + "appointments/" + usrData.getId() + "/"+user.getUserName() +"/notaccept'>Decline Appointment</a></p>";

        sendMail.sendHtmlMail(user.getEmail(), "Your appointment details of MSBTE", s2,syllabusFile);
        userData.setCurrent_status("Appointment Sent");
        userData.setStatus_date(LocalDateTime.now()+"");
        userData.setAppointment_sent_date(LocalDateTime.now()+"");
        appointmentService.saveUserAppointment(usrData);

        return "redirect:/appointments/individualAppointments";
    }

    @PostMapping("/save")
    @Transactional
    public String saveAppointment(Appointment appointment) {
        String userName = generateUserName(appointment);
        appointment.setOrderNo(userName);

        User user = userService.getUserByUserName(userName);
        if (user == null) {
            user = new User();
        }
        setupUserDetails(user, appointment, userName);
        userService.saveUser(user);

        appointmentService.saveAppointment(appointment);

        String emailBody = generateEmailBody(appointment, userName);
        sendMail.sendHtmlmail(appointment.getEmail(), "Your appointment details of MSBTE", emailBody);

        return "redirect:/appointments/list";
    }

    @GetMapping("/edit/{id}")
    public String editAppointment(@PathVariable Long id, Model model) {
        model.addAttribute("appointment", appointmentService.getAppointmentById(id));
        model.addAttribute("coursesList", courseService.getAllCourses());
        model.addAttribute("collegeList", collegeService.getAllColleges());
        return "newappointment";
    }

    @GetMapping("/delete/{id}")
    public String deleteAppointment(@PathVariable Long id) {
        appointmentService.cancelAppointmentById(id);
        return "redirect:/appointments/list";
    }

    @GetMapping("/{orderId}/{userName}/accept")
    public String acceptAppointment(@PathVariable Long orderId, @PathVariable String userName,Model model) {
        UserData appointment = appointmentService.getAppointmentDetailsById(orderId);
        String customPassword = RandomUtils.nextLong(10000, 99999) + "";
        updateAppointmentStatus(orderId, userName,"Accepted",customPassword);
        model.addAttribute("acceptStatus", "Accepted");
        Roles roles = rolesService.getRoleDetails(appointment.getRole_id());
        Subjects subjects = subjectsService.getSubjectById(appointment.getSubject_id()+"");

        User user = userService.getUserByUserName(userName);
        String emailBody = "<html><body>" +
                "<p>You are appointed as a <strong>" + roles.getRole() + "</strong> for the " +
                subjects.getSubject_name() + ".</p>" +
                "<p><a href='" + appUrl + "'>Click here to login</a></p>" +
                "<p><strong>Username:</strong> " + userName + "</p>" +
                "<p><strong>Password:</strong> " + customPassword + "</p>" +
                "<p>Thanks,</p>" +
                "<p><strong>MSBTE</strong></p>" +
                "</body></html>";
        sendMail.sendHtmlmail(user.getEmail(), "Your appointment details of MSBTE", emailBody);
        return "appointment-status";
    }

    @GetMapping("/{orderId}/{userName}/decline")
    public String declineAppointment(@PathVariable Long orderId,String userName, Model model) {
        updateAppointmentStatus(orderId, userName,"Declined","123");
        model.addAttribute("acceptStatus", "Declined");
        return "appointment-status";
    }

    @GetMapping("/resend/{id}")
    public String resendAppointmentMail(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        User user = userService.getUserByUserName(appointment.getOrderNo());

        // Reset password
        String customPassword = RandomUtils.nextLong(10000, 99999) + "";
        user.setPassword(passwordEncoder.encode(customPassword));
        userService.saveUser(user);

        String emailBody = generateEmailBody(appointment, appointment.getOrderNo());
        sendMail.sendHtmlmail(appointment.getEmail(), "Your appointment details of MSBTE", emailBody);

        return "redirect:/appointments/list";
    }

    // ------------------ 🔹 HELPER METHODS ------------------

    private String generateUserName(Appointment appointment) {
        if (appointment.getId() != null) {
            return appointment.getOrderNo();
        }
        return (appointment.getRole_id() == 2 ? "S" : "M") + RandomUtils.nextLong(10000, 99999);
    }

    private String generateUserNameByRole(int roleId) {

        return (roleId == 2 ? "S" : "M") + RandomUtils.nextLong(10000, 99999);
    }
    private void setupUserDetails(User user, Appointment appointment, String userName) {
        user.setIsActive(0);
        user.setRoleId(appointment.getRole_id());
        user.setUserName(userName);
        user.setFirstName(appointment.getName());
        user.setEmail(appointment.getEmail());
        user.setPassword(passwordEncoder.encode(RandomUtils.nextLong(10000, 99999) + ""));
    }

    private void updateAppointmentStatus(Long orderId,String userName, String status,String password) {
        UserData appointment = appointmentService.getAppointmentDetailsById(orderId);
        appointment.setStatus_date(LocalDateTime.now().toString());
        appointment.setCurrent_status(status);


        appointmentService.saveUserAppointment(appointment);

        if ("Accepted".equals(status)) {
            User user = userService.getUserByUserName(userName);
            user.setIsActive(1);
            user.setPassword(passwordEncoder.encode(password));
            userService.saveUser(user);
        }
    }

    private String generateEmailBody(Appointment appointment, String userName) {
        Roles roles = rolesService.getRoleDetails(appointment.getRole_id());
        Subjects subjects = subjectsService.getSubjectById(appointment.getSubject_id());
        Course course = courseService.getCourseDetailsById(subjects.getCourseId());
        String customPassword = RandomUtils.nextLong(10000, 99999) + "";
        SmsEmailTemplate s11 = smsEmailTemplateService.getEmailTemplateDetails();
        LocalDate currentDate = LocalDate.now();
        CollegeEntity collegeEntity = collegeService.getCollegeById(appointment.getCollege_id());
        // Define the desired date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Format the date to the desired format
        String formattedDate = currentDate.format(formatter);

        Context context = new Context();
        context.setVariable("date", formattedDate);
        context.setVariable("name", appointment.getName());
        context.setVariable("collegeCode", collegeEntity.getCollegeCode());
        context.setVariable("subjectCode", subjects.getSubjectCode());
        context.setVariable("subjectName", subjects.getSubject_name());
        context.setVariable("yearCourse", subjects.getYear() + "/" + course.getCourse_name());
        context.setVariable("submissionDate", appointment.getSubmission_date());

        String s1 = templateEngine.process("email-template", context);

        //  String emailBody =String.format(s1,formattedDate,appointment.getName(),appointment.getCollege_code(),
        //        appointment.getSubject_code(),subjects.getSubject_name(),   course.getCourse_name(),"02-03-2025");

        return s1 + "<p>Please click on the link below to accept your appointment :</p>" +
                "<p><a href='" + appUrl + "appointments/" + userName + "/accept'>Accept Appointment</a></p>" +
                //   "<p><strong>Username:</strong> " + userName + "</p>" +
                // "<p><strong>Password:</strong> " + customPassword + "</p>" +
                "<p>If you are not willing to accept this appointment, please click the link below:</p>" +
                "<p><a href='" + appUrl + "appointments/" + userName + "/notaccept'>Decline Appointment</a></p>";
    }


    // Process Selected Records
    @PostMapping("/processSelected")
    public String processSelected(@RequestParam("selectedIds") List<Long> selectedIds, Model model) {
        if (selectedIds.isEmpty()) {
            model.addAttribute("message", "No records selected for processing.");
            return "redirect:/upload";
        }

       List<AppointmentVo> appointmentVos=appointmentService.getAppointmentDetailsList(selectedIds);
       for(AppointmentVo ap:appointmentVos){
           Roles roles = rolesService.getRoleDetails(ap.getRole_id());
           Subjects subjects = subjectsService.getSubjectById(ap.getSubject_id()+"");
           Course course = courseService.getCourseDetailsById(subjects.getCourseId());
           String customPassword = RandomUtils.nextLong(10000, 99999) + "";
           SmsEmailTemplate s11 = smsEmailTemplateService.getEmailTemplateDetails();
           LocalDate currentDate = LocalDate.now();
           CollegeEntity collegeEntity = collegeService.getCollegeById(ap.getCollege_id()+"");
           // Define the desired date format
           DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

           // Format the date to the desired format
           String formattedDate = currentDate.format(formatter);

           Context context = new Context();
           context.setVariable("date", formattedDate);
           context.setVariable("name", ap.getFirst_name()+" "+ap.getLast_name());
           context.setVariable("collegeCode", collegeEntity.getCollegeCode());
           context.setVariable("subjectCode", subjects.getSubjectCode());
           context.setVariable("subjectName", subjects.getSubject_name());
           context.setVariable("yearCourse", subjects.getYear() + "/" + course.getCourse_name());
           context.setVariable("submissionDate", ap.getLast_date_to_submit());

           String s1 = templateEngine.process("email-template", context);

           //  String emailBody =String.format(s1,formattedDate,appointment.getName(),appointment.getCollege_code(),
           //        appointment.getSubject_code(),subjects.getSubject_name(),   course.getCourse_name(),"02-03-2025");
String syllabusFile=filePath+ File.separator+"1.pdf";
           String s2= s1 + "<p>Please click on the link below to accept your appointment :</p>" +
                   "<p><a href='" + appUrl + "appointments/" + ap.getId() + "/"+ap.getUser_name() +"/accept'>Accept Appointment</a></p>" +
                   //   "<p><strong>Username:</strong> " + userName + "</p>" +
                   // "<p><strong>Password:</strong> " + customPassword + "</p>" +
                   "<p>If you are not willing to accept this appointment, please click the link below:</p>" +
                   "<p><a href='" + appUrl + "appointments/" + ap.getId() + "/"+ap.getUser_name() +"/notaccept'>Decline Appointment</a></p>";

           sendMail.sendHtmlMail(ap.getEmail(), "Your appointment details of MSBTE", s2,syllabusFile);

            UserData userData=appointmentService.getAppointmentDetailsById((long) ap.getId());
            userData.setCurrent_status("Appointment Sent");
           userData.setStatus_date(LocalDateTime.now()+"");
           userData.setAppointment_sent_date(LocalDateTime.now()+"");
           appointmentService.saveUserAppointment(userData);

        }
        return "redirect:/upload";
    }
}

