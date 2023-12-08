package com.cc.creativecraze.service;

import com.cc.creativecraze.dto.UserDto;
import com.cc.creativecraze.model.Role;
import com.cc.creativecraze.model.User;
import com.cc.creativecraze.repository.RoleRepository;
import com.cc.creativecraze.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class UserServiceImpl implements UserService{
private final UserRepository userRepository;
private final RoleRepository roleRepository;
private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository= roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setNumber(userDto.getNumber());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Role modelRole = roleRepository.findByName("Artist");
        if (modelRole == null){
            modelRole = new Role();
            modelRole.setName("Artist");
            roleRepository.save(modelRole);
        }
        List<Role> roles = new ArrayList<>();
        roles.add(modelRole);
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());

    }

    private UserDto mapToUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setNumber(user.getNumber());
        return userDto;
    }

    private Date calculateExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.getTime();
    }
}
