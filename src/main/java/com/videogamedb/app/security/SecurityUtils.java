package com.videogamedb.app.security;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public static String getCurrentUserRole() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserPrincipal) {
            return ((UserPrincipal) principal).getAuthorities().stream()
                    .findFirst()
                    .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", ""))
                    .orElse("USER");
        } else {
            return "USER";
        }
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(getCurrentUserRole());
    }
}