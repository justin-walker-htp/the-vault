package com.walker.the_vault;

import com.walker.the_vault.model.User;
import com.walker.the_vault.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "VAULT_ENCRYPTION_KEY=TestingKey12345678901234567890")
class TheVaultApplicationTests {

	@Autowired
	private UserRepository userRepository;

	@Test
	void contextLoads() {
		assertNotNull(userRepository);
	}

	@Test
	void testUserSaveAndRetrieve() {
		User newUser = User.builder()
				.username("test_pilot")
				.password("secure123")
				.email("pilot@test.com")
				.role("USER")
				.build();

		User savedUser = userRepository.save(newUser);

		assertNotNull(savedUser.getId());

		User foundUser = userRepository.findByUsername("test_pilot").orElse(null);

		assertNotNull(foundUser);
		assertEquals("test_pilot", foundUser.getUsername());

		userRepository.delete(savedUser);
	}

	@Autowired
	private com.walker.the_vault.service.UserService userService; // Inject the Service

	@Test
	void testDuplicateUser() {
		// 1. Create User A
		User user1 = User.builder()
				.username("clone_warrior")
				.email("clone@wars.com")
				.password("password")
				.role("USER")
				.build();

		// 2. Save User A
		userService.registerUser(user1);

		// Create User B (Same username)
		User user2 = User.builder()
				.username("clone_warrior")
				.email("clone@wars.com")
				.password("password")
				.role("USER")
				.build();

		// 4. Expect an Exception when saving User B
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			userService.registerUser(user2);
		});

		// 5. Verify the error message
		assertEquals("Username is already taken", exception.getMessage());

		// Cleanup
		userRepository.delete(user1);
	}

}
