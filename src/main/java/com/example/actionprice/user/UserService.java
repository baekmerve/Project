package com.example.actionprice.user;

import com.example.actionprice.user.dto.UserListDTO;
import com.example.actionprice.user.forms.UserRegisterForm;

public interface UserService {
  boolean createUser(UserRegisterForm userRegisterForm);
  boolean checkUserExistsWithUsername(String username);
  boolean checkUserExistsWithEmail(String email);
  boolean checkUsernameAndEmailExists(String username, String email);
  UserListDTO getUserList(String keyword, int pageNumber);
  boolean changePassword(String username, String newPassword);
}
