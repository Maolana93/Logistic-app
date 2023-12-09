package org.logitrack.services;

import org.logitrack.dto.response.ApiResponse;

public interface AdminService {
    ApiResponse adminLogin(String email, String password);
}
