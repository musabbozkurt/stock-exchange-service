package com.mb.stockexchangeservice.integration_tests.api.controller;

import com.mb.stockexchangeservice.api.request.ApiStockExchangeRequest;
import com.mb.stockexchangeservice.api.request.ApiStockRequest;
import com.mb.stockexchangeservice.api.request.ApiUserAuthRequest;
import com.mb.stockexchangeservice.api.response.ApiStockExchangeResponse;
import com.mb.stockexchangeservice.api.response.ApiStockResponse;
import com.mb.stockexchangeservice.api.response.JwtResponse;
import com.mb.stockexchangeservice.base.BaseUnitTest;
import com.mb.stockexchangeservice.exception.BaseException;
import com.mb.stockexchangeservice.exception.StockExchangeServiceErrorCode;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test-containers")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StockExchangeControllerIntegrationTests extends BaseUnitTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public void setUp() {
        // Arrange
        ApiUserAuthRequest apiUserAuthRequest = new ApiUserAuthRequest();
        apiUserAuthRequest.setUsername("admin_user");
        apiUserAuthRequest.setPassword("test1234");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ApiUserAuthRequest> entity = new HttpEntity<>(apiUserAuthRequest, headers);

        // Act
        ResponseEntity<JwtResponse> response = restTemplate.exchange("/auth/signin", HttpMethod.POST, entity, JwtResponse.class);

        JwtResponse jwtResponse = response.getBody();

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(jwtResponse);
        String token = jwtResponse.getToken();
        assertNotNull(token);

        // Create RestTemplate with custom header interceptor
        restTemplate.getRestTemplate().setInterceptors(Collections.singletonList((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer ".concat(token));
            return execution.execute(request, body);
        }));
    }

    @Test
    @Order(value = 1)
    void testCreateStockExchange() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        ApiStockExchangeRequest request = new ApiStockExchangeRequest();
        request.setName("Toronto Stock Exchange");
        request.setDescription("Toronto Stock Exchange Description");

        ResponseEntity<ApiStockExchangeResponse> response = restTemplate.exchange("/stock-exchange/", HttpMethod.POST, new HttpEntity<>(request, headers), ApiStockExchangeResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals(request.getName(), response.getBody().getName());
        assertEquals(request.getDescription(), response.getBody().getDescription());
    }

    @Test
    @Order(value = 2)
    void testAddStockToStockExchange() {
        ApiStockRequest stockRequest = new ApiStockRequest();
        stockRequest.setName("AAPL");
        stockRequest.setDescription("APPLE Stock Description");
        stockRequest.setCurrentPrice(BigDecimal.valueOf(100.0));

        ResponseEntity<ApiStockExchangeResponse> response = restTemplate.exchange("/stock-exchange/New York Stock Exchange", HttpMethod.POST, new HttpEntity<>(stockRequest), ApiStockExchangeResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(value = 3)
    void testAddStockToStockExchange_ShouldFail_WhenStockIsNotFound() {
        ApiStockRequest stockRequest = new ApiStockRequest();
        stockRequest.setName("AAPLE");
        stockRequest.setDescription("APPLE Description");
        stockRequest.setCurrentPrice(BigDecimal.valueOf(100.0));

        ResponseEntity<BaseException> response = restTemplate.exchange("/stock-exchange/New York Stock Exchange", HttpMethod.POST, new HttpEntity<>(stockRequest), BaseException.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        Assertions.assertEquals(StockExchangeServiceErrorCode.STOCK_NOT_FOUND, response.getBody().getErrorCode());
    }

    @Test
    @Order(value = 4)
    void testGetStocksByStockExchangeName() {
        ResponseEntity<List<ApiStockResponse>> response = restTemplate.exchange("/stock-exchange/New York Stock Exchange", HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(value = 5)
    void testGetStocksByStockExchangeName_ShouldFail_WhenStockExchangeIsNotFound() {
        ResponseEntity<BaseException> response = restTemplate.getForEntity("/stock-exchange/Bombay Stock Exchange", BaseException.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        Assertions.assertEquals(StockExchangeServiceErrorCode.STOCK_EXCHANGE_NOT_FOUND, response.getBody().getErrorCode());
    }

    @Test
    @Order(value = 6)
    void testDeleteStockFromStockExchange() {
        ResponseEntity<ApiStockExchangeResponse> response = restTemplate.exchange("/stock-exchange/New York Stock Exchange/AAPL", HttpMethod.DELETE, null, ApiStockExchangeResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(value = 7)
    void testDeleteStockFromStockExchange_ShouldFail_WhenStockExchangeIsNotFound() {
        ResponseEntity<BaseException> response = restTemplate.exchange("/stock-exchange/Bombay Stock Exchange/APPL", HttpMethod.DELETE, null, BaseException.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        Assertions.assertEquals(StockExchangeServiceErrorCode.STOCK_EXCHANGE_NOT_FOUND, response.getBody().getErrorCode());
    }

    @Test
    @Order(value = 8)
    void testDeleteStockFromStockExchange_ShouldFail_WhenStockIsNotFound() {
        ResponseEntity<BaseException> response = restTemplate.exchange("/stock-exchange/New York Stock Exchange/APPLE", HttpMethod.DELETE, null, BaseException.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        Assertions.assertEquals(StockExchangeServiceErrorCode.STOCK_NOT_FOUND, response.getBody().getErrorCode());
    }
}
