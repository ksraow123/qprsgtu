package in.coempt.controller;

import in.coempt.service.AppointmentService;
import in.coempt.service.CSVService;
import in.coempt.service.SetterModeratorService;
import in.coempt.vo.SetterModeratorMappingVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CSVController {

    private final CSVService csvService;
    private final AppointmentService appointmentService;
    private final SetterModeratorService setterModeratorService;

    @GetMapping("/upload")
    public String showUploadForm(Model model) {

      //  model.addAttribute("appointmentList",appointmentService.getAllAppointmentDetails());

        model.addAttribute("appointmentList",appointmentService.getAppointmentDshBoard());


        model.addAttribute("page","uploadNew");
        return "main";

    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload.");
            model.addAttribute("page","uploadNew");
            return "main";
        }

        try {
            csvService.saveCSV(file);
            model.addAttribute("message", "File uploaded successfully!");
        } catch (Exception e) {
            model.addAttribute("message", "Failed to upload file: " + e.getMessage());
        }
//model.addAttribute("page","upload");
        return "redirect:/upload";
    }

    @GetMapping("/setterModeratorMapping")
    public String setterModeratorMapping(Model model) {
        List<SetterModeratorMappingVo> list=setterModeratorService.getModeratorSetterMappingDetails();
        model.addAttribute("setterModeratorMappingList",list);
        model.addAttribute("page","mappingFileUpload");
        return "main";
    }

    @PostMapping("/mappingUpload")
    public String mappingUpload(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Please select a file to upload.");
            model.addAttribute("page","mappingFileUpload");
            return "main";
        }

        try {
            csvService.saveMappingCSV(file);
            model.addAttribute("message", "File uploaded successfully!");
        } catch (Exception e) {
            model.addAttribute("message", "Failed to upload file: " + e.getMessage());
        }
        //model.addAttribute("page","upload");
        return "redirect:/setterModeratorMapping";
    }
}
