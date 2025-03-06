package in.coempt.service.impl;

import in.coempt.dao.DashBoardRepository;
import in.coempt.service.DashBoardService;
import in.coempt.vo.QPSetterDashBoardVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashBoardServiceImpl implements DashBoardService {
    @Autowired
    private DashBoardRepository dashBoardRepository;
    @Override
    public List<QPSetterDashBoardVo> getQPSetterDashBord(String userName, Long userId) {
        return dashBoardRepository.getSetterDashBoard(userName,userId);
    }

    @Override
    public List<QPSetterDashBoardVo> getQPModeratorDashBord(String userName, Long userId) {
        return dashBoardRepository.getModeratorDashBoard(userName,userId);
    }
    @Override
    public List<QPSetterDashBoardVo> getSetWiseQPDashBoard(Long userId) {
        return dashBoardRepository.getSetWiseQPDashBoard(userId);
    }

    @Override
    public List<QPSetterDashBoardVo> getSetWiseReviewerQPDashBoard(String userName, Long userId) {
        return dashBoardRepository.getSetWiseReviewerQPDashBoard(userId);
    }
}
