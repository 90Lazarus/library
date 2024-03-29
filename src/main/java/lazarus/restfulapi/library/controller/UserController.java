package lazarus.restfulapi.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lazarus.restfulapi.library.documentation.UserPDFExporter;
import lazarus.restfulapi.library.exception.InvalidRoleException;
import lazarus.restfulapi.library.exception.PasswordsDontMatchException;
import lazarus.restfulapi.library.exception.ResourceNotFoundException;
import lazarus.restfulapi.library.exception.UniqueViolationException;
import lazarus.restfulapi.library.model.dto.NewUserDTO;
import lazarus.restfulapi.library.model.dto.PasswordResetDTO;
import lazarus.restfulapi.library.model.dto.UserDTO;
import lazarus.restfulapi.library.model.mapper.UserMapper;
import lazarus.restfulapi.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    @Autowired private UserService userService;
    @Autowired private UserMapper userMapper;

    @GetMapping("/users")
    @Operation(summary = "Retrieve the pageable list of all available users in the database, optionally sorted by parameters")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found users in the database!")})
    public List<UserDTO> getUsers(@RequestParam(required = false, defaultValue = "0") Integer page,
                                  @RequestParam(required = false, defaultValue = "10") Integer size,
                                  @RequestParam(required = false, defaultValue = "ASC") Sort.Direction direction,
                                  @RequestParam(required = false, defaultValue = "id") String sortBy) throws ResourceNotFoundException {
        return userService.readUsers(page, size, direction, sortBy);
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Retrieve the information about a user in the database with a specified id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found the user!")})
    public UserDTO getAUser(@Parameter(description = "User's id") @PathVariable Long userId) throws ResourceNotFoundException {
        return userService.readAUser(userId);
    }

    @GetMapping("/user")
    @Operation(summary = "Retrieve the information about the user who is currently logged in")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Data retrieved!")})
    public NewUserDTO getUserInfo() {
        return userService.readUserInfo();
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User registered!")})
    public UserDTO registerANewUser(@RequestBody @Valid NewUserDTO newUserDTO) throws UniqueViolationException, PasswordsDontMatchException {
        return userService.registerANewUser(newUserDTO);
    }

    @PutMapping("/user")
    @Operation(summary = "Modify the information about the user who is currently logged in")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Data updated!")})
    public UserDTO modifyUser(@RequestBody @Valid NewUserDTO newUserDTO) throws UniqueViolationException, PasswordsDontMatchException {
        return userService.modifyUserData(newUserDTO);
    }

    @PutMapping("/users/{userId}")
    @Operation(summary = "Modify a role for a user with a specified id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User's role modified!")})
    public UserDTO modifyRole(@Parameter(description = "User's id") @PathVariable Long userId, @Parameter(description = "String of a new User's role") @RequestBody @Valid String newUserRole) throws ResourceNotFoundException, InvalidRoleException {
        return userService.changeUserRole(userId, newUserRole);
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "Delete a user with a specified id from the database")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "User deleted!")})
    public void deleteAUser(@Parameter(description = "User's id") @PathVariable Long userId) throws ResourceNotFoundException {
        userService.deleteAUser(userId);
    }

    @PostMapping("/forgot_password")
    @Operation(summary = "Send a password reset token to a user provided email")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Password Reset Token sent!")})
    public void userForgotPassword(@RequestBody String userEmail) throws ResourceNotFoundException {
        userService.sendPasswordResetToken(userEmail);
    }

    @PutMapping("/reset_password")
    @Operation(summary = "Save a new user password")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Password updated!")})
    public UserDTO resetUserPassword(@RequestParam String token, @RequestBody @Valid PasswordResetDTO userUpdatedPassword) throws ResourceNotFoundException, PasswordsDontMatchException {
        return userService.resetUserPassword(token, userUpdatedPassword);
    }

    @GetMapping("/users/export/pdf")
    @Operation(summary = "Generate a PDF document with the list of all of the users in the database")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "PDF generated!")})
    public void exportUsersToPDF(HttpServletResponse response) throws IOException, ResourceNotFoundException {
        List<UserDTO> listUsersDTO = userService.readUsers(0, 20, Sort.Direction.ASC, "id");
        UserPDFExporter exporter = new UserPDFExporter(userMapper.userDTOsToUsers(listUsersDTO));
        exporter.export(response);
    }
}