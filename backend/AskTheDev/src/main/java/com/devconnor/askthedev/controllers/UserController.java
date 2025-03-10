package com.devconnor.askthedev.controllers;

import com.devconnor.askthedev.controllers.response.ATDUserResponse;
import com.devconnor.askthedev.models.User;
import com.devconnor.askthedev.services.AuthenticationService;
import com.devconnor.askthedev.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.devconnor.askthedev.utils.Constants.INVALID_SESSION_MESSAGE;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final AuthenticationService authenticationService;

    @GetMapping("/{id}")
    public ResponseEntity<ATDUserResponse> getUserById(@PathVariable Long id) {
        ATDUserResponse atdUserResponse = new ATDUserResponse();

        if (!authenticationService.verifyUserSession(id)) {
            atdUserResponse.setMessage(INVALID_SESSION_MESSAGE);
            return new ResponseEntity<>(atdUserResponse, HttpStatus.UNAUTHORIZED);
        }

        User user = userService.getUserById(id);
        atdUserResponse.setUser(user);

        return ResponseEntity.ok().body(atdUserResponse);
    }
}
