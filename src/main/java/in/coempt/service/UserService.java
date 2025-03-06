package in.coempt.service;

import in.coempt.entity.User;

public interface UserService {
    public void saveUser(User user);

    User getUserByUserName(String orderId);
    public boolean changePassword(String username, String oldPassword, String newPassword);

    public boolean resetPassword(String token, String newPassword);
    public void sendPasswordResetLink(String email);

    User getUserByMobileNo(String mobileNo);

    User getUserById(String userId);
}
