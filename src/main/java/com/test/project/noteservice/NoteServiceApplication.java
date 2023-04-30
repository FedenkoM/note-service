package com.test.project.noteservice;

import com.test.project.noteservice.entity.Role;
import com.test.project.noteservice.entity.User;
import com.test.project.noteservice.repository.UserRepository;
import com.test.project.noteservice.utils.sequence.SequenceGeneratorService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(title = "Note API"),
		servers = @Server(url = "${server.servlet.context-path}")
)
public class NoteServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NoteServiceApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner commandLineRunner(UserRepository userRepository, SequenceGeneratorService sequenceGenerator) {
		return args -> {
			var admin = new User();
			admin.setId(sequenceGenerator.generateSequence(User.SEQUENCE_NAME));
			admin.setEmail("admin@mail.com");
			admin.setPassword(passwordEncoder().encode("password"));
			admin.setFirstName("Admin");
			admin.setLastName("Admin");
			admin.addRole(Role.ROLE_ADMIN);
			if (userRepository.existsByEmail(admin.getEmail())) return;
			userRepository.save(admin);
		};
	}
}
