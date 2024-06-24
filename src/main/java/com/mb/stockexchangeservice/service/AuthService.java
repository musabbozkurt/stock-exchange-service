package com.mb.stockexchangeservice.service;

import com.mb.stockexchangeservice.api.request.ApiUserAuthRequest;
import com.mb.stockexchangeservice.api.response.JwtResponse;

public interface AuthService {

    JwtResponse getJwtResponseResponseEntity(ApiUserAuthRequest apiUserAuthRequest);
}
