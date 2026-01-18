package com.hymer.hymarket;

import com.hymer.hymarket.Repository.RoleRepository;
import com.hymer.hymarket.Repository.ServiceCategoryRepo;
import com.hymer.hymarket.Repository.ServiceTypeRepo;
import com.hymer.hymarket.Repository.UserRepository;
import com.hymer.hymarket.model.Roles;
import com.hymer.hymarket.model.ServiceCategory;
import com.hymer.hymarket.model.ServiceType;
import com.hymer.hymarket.model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class HyMarketApplication {
    public static void main(String[] args) {

        SpringApplication.run(HyMarketApplication.class, args);

    }


    @Bean
    CommandLineRunner run(RoleRepository roleRepository) {
        return args -> {
            // Check if roles already exist before adding them
            if (roleRepository.findByName("ROLE_USER").isEmpty()) {
                roleRepository.save(new Roles("ROLE_USER"));
            }
            if (roleRepository.findByName("ROLE_PROVIDER").isEmpty()) {
                roleRepository.save(new Roles("ROLE_PROVIDER"));
            }
            if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
                roleRepository.save(new Roles("ROLE_ADMIN"));
            }
            System.out.println("--- DEFAULT ROLES CREATED ---");
        };


    }
    @Bean
    public CommandLineRunner initData(ServiceCategoryRepo categoryRepo, ServiceTypeRepo typeRepo) {
        return args -> {
            if (categoryRepo.count() == 0) {
                // 1. Create the Two Main Categories
                ServiceCategory semiLux = categoryRepo.save(new ServiceCategory("Semi-Luxurious"));
                ServiceCategory lux = categoryRepo.save(new ServiceCategory("Luxurious"));

                // 2. Add Services INSIDE Semi-Luxurious
                typeRepo.save(new ServiceType("Salons", semiLux));
                typeRepo.save(new ServiceType("Local Restaurants", semiLux));
                typeRepo.save(new ServiceType("Budget Hotels", semiLux));
                typeRepo.save(new ServiceType("Normal Car Rentals", semiLux));
                typeRepo.save(new ServiceType("Electrician", semiLux));
                typeRepo.save(new ServiceType("Plumbing", semiLux));
                typeRepo.save(new ServiceType("House Help", semiLux));


                // 3. Add Services INSIDE Luxurious
                typeRepo.save(new ServiceType("Luxury Spas", lux));
                typeRepo.save(new ServiceType("Fine Dining", lux));
                typeRepo.save(new ServiceType("5-Star Hotels", lux));
                typeRepo.save(new ServiceType("Premium Car Rentals", lux));

                System.out.println("--- HIERARCHY CREATED SUCCESSFULLY ---");
            }
        };
    }
    @Bean
    public CommandLineRunner AdminUserCreate(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder  passwordEncoder) {
        return args -> {
            String AdminEmail = "admin123@gmail.com";
            if(userRepository.findByEmail(AdminEmail).isEmpty()) {
                User admin = new User();
                admin.setEmail(AdminEmail);
                admin.setFirstName("Super");
                admin.setLastName("Admin");
                admin.setPassword(passwordEncoder.encode("admin@123"));
                admin.setPhoneNumber("0123456789");
                admin.setUsername("adminAshish");

                Roles adminRole = roleRepository.findByName("ROLE_ADMIN")
                        .orElseThrow(() -> new RuntimeException("Role Admin Not Found"));
                Roles adminAsUser = roleRepository.findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("Role User Not Found"));
                Set<Roles> roles = new HashSet<>();
                roles.add(adminRole);
                roles.add(adminAsUser);
                admin.setRoles(roles);

                userRepository.save(admin);
                System.out.println("--- ADMIN USER CREATED: admin123@gmail.com / admin123 ---");
            }

        };
    }
}



