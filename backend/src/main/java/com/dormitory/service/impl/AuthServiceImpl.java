package com.dormitory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dormitory.dto.request.LoginRequest;
import com.dormitory.dto.request.RegisterRequest;
import com.dormitory.dto.response.JwtResponse;
import com.dormitory.dto.response.UserResponse;
import com.dormitory.entity.Permission;
import com.dormitory.entity.Role;
import com.dormitory.entity.User;
import com.dormitory.exception.BusinessException;
import com.dormitory.mapper.UserMapper;
import com.dormitory.security.JwtTokenProvider;
import com.dormitory.service.AuthService;
import com.dormitory.service.PermissionService;
import com.dormitory.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PermissionService permissionService;
    private final RoleService roleService;
    
    @Override
    public JwtResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectList(wrapper).stream().findFirst().orElse(null);
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 获取用户角色和权限
        List<Role> roles = roleService.getUserRoles(user.getId());
        List<Permission> permissions = permissionService.getUserPermissions(user.getId());
        
        List<String> roleCodes = roles.stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toList());
        
        List<String> permissionCodes = permissions.stream()
                .map(Permission::getCode)
                .collect(Collectors.toList());
        
        return JwtResponse.builder()
                .token(jwt)
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .userType(user.getUserType())
                .roles(roleCodes)
                .permissions(permissionCodes)
                .build();
    }
    
    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        if (request.getStudentId() != null) {
            wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getStudentId, request.getStudentId());
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException("学号已存在");
            }
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStudentId(request.getStudentId());
        user.setDepartment(request.getDepartment());
        user.setClassName(request.getClassName());
        user.setUserType("STUDENT");
        user.setStatus("ACTIVE");
        user.setCreateTime(LocalDateTime.now());
        
        userMapper.insert(user);
        
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .studentId(user.getStudentId())
                .department(user.getDepartment())
                .className(user.getClassName())
                .userType(user.getUserType())
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .build();
    }
    
    @Override
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectList(wrapper).stream().findFirst().orElse(null);
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .studentId(user.getStudentId())
                .department(user.getDepartment())
                .className(user.getClassName())
                .userType(user.getUserType())
                .status(user.getStatus())
                .avatar(user.getAvatar())
                .createTime(user.getCreateTime())
                .build();
    }
    
    @Override
    public JwtResponse refreshToken(String oldToken) {
        if (oldToken.startsWith("Bearer ")) {
            oldToken = oldToken.substring(7);
        }
        
        if (!tokenProvider.validateToken(oldToken)) {
            throw new BusinessException("无效的Token");
        }
        
        String username = tokenProvider.getUsernameFromToken(oldToken);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectList(wrapper).stream().findFirst().orElse(null);
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                username, null, Collections.emptyList()
        );
        String newToken = tokenProvider.generateToken(authentication);
        
        // 获取用户角色和权限
        List<Role> roles = roleService.getUserRoles(user.getId());
        List<Permission> permissions = permissionService.getUserPermissions(user.getId());
        
        List<String> roleCodes = roles.stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toList());
        
        List<String> permissionCodes = permissions.stream()
                .map(Permission::getCode)
                .collect(Collectors.toList());
        
        return JwtResponse.builder()
                .token(newToken)
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .userType(user.getUserType())
                .roles(roleCodes)
                .permissions(permissionCodes)
                .build();
    }
}
