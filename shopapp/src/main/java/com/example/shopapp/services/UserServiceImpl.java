package com.example.shopapp.services;

import com.example.shopapp.components.JwtUtils;
import com.example.shopapp.dtos.UserDTO;
import com.example.shopapp.exceptions.DataNotFoundException;
import com.example.shopapp.exceptions.InvalidParamException;
import com.example.shopapp.models.Role;
import com.example.shopapp.models.User;
import com.example.shopapp.repositories.RoleRepository;
import com.example.shopapp.repositories.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public User createUser(UserDTO userDTO) throws DataNotFoundException {
        // register user
        // create a new user and return it
        String phoneNumber = userDTO.getPhoneNumber();

        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }

        // convert from UserDTO to User
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(phoneNumber)
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .build();
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found"));
        newUser.setRole(role);
        if ((userDTO.getFacebookAccountId() == null || userDTO.getFacebookAccountId().isEmpty())
                && (userDTO.getGoogleAccountId() == null || userDTO.getGoogleAccountId()
                .isEmpty())) {
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        } else {
            newUser.setPassword("");
        }
        return userRepository.save(newUser);
    }

    @Override
    public String loginUser(String phoneNumber, String password) throws InvalidParamException {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isEmpty()) {
            throw new BadCredentialsException("Invalid phone number or password");
        }
        User existingUser = optionalUser.get();
        // check if the user has a password
        // only check if user signs in with phone number and password
        // not check if user signs in with Facebook or Google
        if ((existingUser.getFacebookAccountId() == null || existingUser.getFacebookAccountId()
                .isEmpty())
                && (existingUser.getGoogleAccountId() == null || existingUser.getGoogleAccountId()
                .isEmpty())
                && !passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new BadCredentialsException("Invalid phone number or password");
        }
        // authenticate user with Java Spring Security
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                phoneNumber, password,
                existingUser.getAuthorities());
        authenticationManager.authenticate(authenticationToken);
        // return the generated token
        return jwtUtils.generateToken(existingUser);
    }
}
