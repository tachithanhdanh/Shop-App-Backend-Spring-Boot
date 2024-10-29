package com.example.shopapp.services;

import com.example.shopapp.dtos.UserDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.exceptions.InvalidParamException;
import com.example.shopapp.models.User;
import org.springframework.stereotype.Service;

public interface UserService {
    User createUser(UserDTO userDTO) throws DataNotFoundException;
    String loginUser(String phoneNumber, String password)
            throws DataNotFoundException, InvalidParamException;
}
