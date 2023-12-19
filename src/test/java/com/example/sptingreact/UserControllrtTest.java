package com.example.sptingreact;

import com.example.sptingreact.controller.UserController;
import com.example.sptingreact.exception.UserNotFoundException;
import com.example.sptingreact.model.User;
import com.example.sptingreact.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // integrer mockito avec junit 5
class UserControllerTest {
// creer un mocl du repo userrrepo, et injecter lrd mocks dans le controleur
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    @Test
    void testGetAllUsers() throws Exception {
        // Arrange
        List<User> users = Arrays.asList(
                new User(1L, "user1", "John Doe", "john@example.com"),
                new User(2L, "user2", "Jane Doe", "jane@example.com")
        );
        when(userRepository.findAll()).thenReturn(users);

        // Act and Assert /// on effecture un requete get vers /users en utilisant mockmvc et on verifie que la rps est ok (200) kanveriifw wach id = 1 o ...
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Jane Doe"));
    }

    @Test
    void testGetUserById() throws Exception {
        // Arrange
        Long userId = 1L;
        User user = new User(userId, "user1", "John Doe", "john@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act and Assert
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        mockMvc.perform(MockMvcRequestBuilders.get("/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("user1"));
    }

    @Test
    void testNewUser() throws Exception {
        // Arrange
        User newUser = new User(null, "newuser", "New User", "newuser@example.com");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // Act and Assert
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        mockMvc.perform(MockMvcRequestBuilders.post("/user")
                        .content(asJsonString(newUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("newuser"));
    }

    @Test
    void testUpdateUser() throws Exception {
        // Arrange
        Long userId = 1L;
        User existingUser = new User(userId, "user1", "John Doe", "john@example.com");
        User updatedUser = new User(userId, "updateduser", "Updated User", "updateduser@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act and Assert
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        mockMvc.perform(MockMvcRequestBuilders.put("/user/{id}", userId)
                        .content(asJsonString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("updateduser"));
    }

    @Test
    void testDeleteUser() throws Exception {
        // Arrange
        Long userId = 1L;
        User existingUser = new User(userId, "user1", "John Doe", "john@example.com");
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act and Assert
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        mockMvc.perform(MockMvcRequestBuilders.delete("/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("User with id " + userId + " has been deleted successfully."));
    }

    // Helper method to convert objects to JSON format
    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
