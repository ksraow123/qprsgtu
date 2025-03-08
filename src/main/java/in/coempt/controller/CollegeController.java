package in.coempt.controller;

import in.coempt.entity.CollegeEntity;
import in.coempt.entity.FacultyData;
import in.coempt.service.CollegeService;
import in.coempt.service.FacultyDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/college")
public class CollegeController {
    @Autowired
    private CollegeService collegeService;


    @GetMapping("/collegeData/{collegeCode}")
    @ResponseBody
    public CollegeEntity getCollegeData(@PathVariable String collegeCode) {
        return collegeService.getCollegeByCode(collegeCode);
    }
}
