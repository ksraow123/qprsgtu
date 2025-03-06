package in.coempt.controller;

import in.coempt.entity.User;
import in.coempt.repository.UserRepository;
import in.coempt.service.DashBoardService;
import in.coempt.util.SecurityUtil;
import in.coempt.vo.QPSetterDashBoardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {
    @Autowired
    private DashBoardService dashBoardService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/setterdashboard")
    public String userDashBoard(Model model) {
        UserDetails user=(UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userRepository.findByUserName(user.getUsername());
      List<QPSetterDashBoardVo>  qpSetterDashBoardList= dashBoardService.getQPSetterDashBord(userEntity.getUserName(),userEntity.getId());

        List<QPSetterDashBoardVo>  setwiseDashBoard= dashBoardService.getSetWiseQPDashBoard(userEntity.getId());
model.addAttribute("qpSetterDashBoardList",qpSetterDashBoardList);
model.addAttribute("setwiseDashBoard",setwiseDashBoard);
        model.addAttribute("page","setterDashBoard");
return "main";

    }

    @GetMapping("/moderatordashboard")
    public String moderatorDashBoard(Model model) {
        UserDetails user=(UserDetails) SecurityUtil.getLoggedUserDetails().getPrincipal();
        User userEntity = userRepository.findByUserName(user.getUsername());
        List<QPSetterDashBoardVo>  qpModeratorDashBoardList= dashBoardService.getQPModeratorDashBord(userEntity.getUserName(),userEntity.getId());
        List<QPSetterDashBoardVo>  setWiseReviewerQPDashBoard= dashBoardService.getSetWiseReviewerQPDashBoard(userEntity.getUserName(),userEntity.getId());

        model.addAttribute("qpModeratorDashBoardList",qpModeratorDashBoardList);
        model.addAttribute("setWiseReviewerQPDashBoard",setWiseReviewerQPDashBoard);
        model.addAttribute("page","moderatorDashBoard");
        return "main";

    }

}
