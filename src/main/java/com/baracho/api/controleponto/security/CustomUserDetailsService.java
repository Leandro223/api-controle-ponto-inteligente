package com.baracho.api.controleponto.security;

import com.baracho.api.controleponto.entities.Funcionario;
import com.baracho.api.controleponto.enums.PerfilEnum;
import com.baracho.api.controleponto.repositories.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

      private FuncionarioRepository funcionarioRepository;

      @Autowired
      public CustomUserDetailsService(FuncionarioRepository funcionarioRepository) {
            this.funcionarioRepository = funcionarioRepository;
      }


      @Override
      public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            Funcionario user = funcionarioRepository.buscarPorNome(username).orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));
            return new User(user.getNome(), user.getSenha(), CustomUserDetailsService.mapRolesToAuthorities(user.getPerfil()));
      }

      private static List<GrantedAuthority> mapRolesToAuthorities(PerfilEnum perfil) {
            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            authorities.add(new SimpleGrantedAuthority(perfil.toString()));
            return authorities;
      }
}
