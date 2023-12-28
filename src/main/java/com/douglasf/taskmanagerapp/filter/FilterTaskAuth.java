package com.douglasf.taskmanagerapp.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.douglasf.taskmanagerapp.user.IUserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    var servletPath = request.getServletPath();

    if (servletPath.startsWith("/tasks/")) {

      /** Get and decode user credentials */
      var authData = request.getHeader("Authorization");
      var AUTH_KEY = "Basic";

      var authValueEncoded = authData.substring(AUTH_KEY.length()).trim();
      byte[] authValueDecoded = Base64.getDecoder().decode(authValueEncoded);

      var credentials = new String(authValueDecoded).split(":");
      String userName = credentials[0];
      String password = credentials[1];

      /** Validate user credentials */
      var userQueryData = this.userRepository.findByUserName(userName);
      if (userQueryData == null) {
        response.sendError(401, "Unauthorized user.");
        return;
      }

      var passwordComparison = BCrypt.verifyer().verify(password.toCharArray(), userQueryData.getPassword());

      if (passwordComparison.verified) {
        request.setAttribute("userId", userQueryData.getId());

        filterChain.doFilter(request, response);
        return;
      }

      response.sendError(401, "Unauthorized user.");
    }

    filterChain.doFilter(request, response);
  }

}
