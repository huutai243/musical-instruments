package vn.iuh.fit.musical_instrument.services;


import vn.iuh.fit.musical_instrument.dto.request.RegistrationDto;
import vn.iuh.fit.musical_instrument.entities.User;

public interface UserService {
    User registerUser(RegistrationDto dto);
    boolean verifyUser(String token);
}

