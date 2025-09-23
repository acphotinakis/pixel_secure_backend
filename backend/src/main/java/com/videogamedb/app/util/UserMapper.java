package com.videogamedb.app.util;

import com.videogamedb.app.dto.UserDTO;
import com.videogamedb.app.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    @Autowired
    private EncryptionUtil encryptionUtil;

    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setUsername(encryptionUtil.decryptField(user.getUsernameEnc()));
        dto.setEmail(encryptionUtil.decryptField(user.getEmailEnc()));
        dto.setFirstName(encryptionUtil.decryptField(user.getFirstNameEnc()));
        dto.setLastName(encryptionUtil.decryptField(user.getLastNameEnc()));
        dto.setEmailMasked(user.getEmailMasked());
        dto.setCreationDate(user.getCreationDate());
        dto.setRole(user.getRole());
        dto.setPlatforms(user.getPlatforms());
        return dto;
    }

    public User toEntity(UserDTO dto) {
        User user = new User();
        user.setUsernameEnc(encryptionUtil.encryptField(dto.getUsername()));
        user.setUsernameHash(encryptionUtil.hashUsername(dto.getUsername()));
        user.setEmailEnc(encryptionUtil.encryptField(dto.getEmail()));
        user.setFirstNameEnc(encryptionUtil.encryptField(dto.getFirstName()));
        user.setLastNameEnc(encryptionUtil.encryptField(dto.getLastName()));
        user.setEmailMasked(dto.getEmailMasked());
        user.setRole(dto.getRole());
        user.setPlatforms(dto.getPlatforms());
        return user;
    }
}