package com.baracho.api.controleponto.controllers;

import com.baracho.api.controleponto.dto.AuthResponseDTO;
import com.baracho.api.controleponto.dto.LoginDto;
import com.baracho.api.controleponto.repositories.FuncionarioRepository;
import com.baracho.api.controleponto.security.JWTGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Access;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

      private AuthenticationManager authenticationManager;
      private FuncionarioRepository funcionarioRepository;
      private PasswordEncoder passwordEncoder;
      private JWTGenerator jwtGenerator;

      @Autowired
      public AuthController(AuthenticationManager authenticationManager, FuncionarioRepository funcionarioRepository, PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator) {
            this.authenticationManager = authenticationManager;
            this.funcionarioRepository = funcionarioRepository;
            this.passwordEncoder = passwordEncoder;
            this.jwtGenerator = jwtGenerator;
      }

      @PostMapping("login")
      public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDto loginDto){
            Authentication authentication = authenticationManager.authenticate(
                      new UsernamePasswordAuthenticationToken(
                                loginDto.getUsername(),
                                loginDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtGenerator.generateToken(authentication);
            return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);

      }
}
