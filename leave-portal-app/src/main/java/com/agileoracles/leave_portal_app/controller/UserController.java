package com.agileoracles.leave_portal_app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/user")
    public Map<String, Object> currentUser(
            Authentication authentication
    ) {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("authenticated", authentication.isAuthenticated());
        response.put("authenticationName", authentication.getName());

        if (authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            response.put("name", oauth2User.getAttribute("name"));
            response.put("email", oauth2User.getAttribute("email"));
            response.put("picture", oauth2User.getAttribute("picture"));
        }

        return response;
    }
}