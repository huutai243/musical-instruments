package vn.iuh.fit.musical_instrument.services;


import vn.iuh.fit.musical_instrument.dto.UserRegistrationDto;
import vn.iuh.fit.musical_instrument.entities.User;

public interface UserService {
    User registerUser(UserRegistrationDto dto);
    boolean verifyUser(String token);
}

