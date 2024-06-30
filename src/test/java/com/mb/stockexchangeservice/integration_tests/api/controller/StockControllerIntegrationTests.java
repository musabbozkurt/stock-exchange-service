package com.mb.stockexchangeservice.integration_tests.api.controller;

import com.mb.stockexchangeservice.api.request.ApiStockRequest;
import com.mb.stockexchangeservice.api.request.ApiUserAuthRequest;
import com.mb.stockexchangeservice.api.response.ApiStockResponse;
import com.mb.stockexchangeservice.api.response.JwtResponse;
import com.mb.stockexchangeservice.base.BaseUnitTest;
import com.mb.stockexchangeservice.config.TestRedisConfiguration;
import com.mb.stockexchangeservice.exception.BaseException;
import com.mb.stockexchangeservice.exception.StockExchangeServiceErrorCode;
import com.mb.stockexchangeservice.mapper.StockMapper;
import com.mb.stockexchangeservice.service.StockService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test-containers")
@TestMethodOrder(OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestRedisConfiguration.class)
public class StockControllerIntegrationTests extends BaseUnitTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StockService stockService;

    @Autowired
    private StockMapper stockMapper;

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
    void testConnectionToDatabase() {
        Assertions.assertNotNull(stockService);
        Assertions.assertNotNull(stockMapper);
    }

    @Test
    @Order(value = 2)
    void testGetAllStocks() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        ResponseEntity<String> response = restTemplate.exchange("/stock/", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(value = 3)
    void testGetStockById() {
        ResponseEntity<ApiStockResponse> response = restTemplate.getForEntity("/stock/AAPL", ApiStockResponse.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        Assertions.assertEquals("AAPL", response.getBody().getName());
    }

    @Test
    @Order(value = 4)
    void testGetStockById_ShouldFail_WhenStockIsNotFound() {
        ResponseEntity<BaseException> response = restTemplate.getForEntity("/stock/AAPLL", BaseException.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        Assertions.assertEquals(StockExchangeServiceErrorCode.STOCK_NOT_FOUND, response.getBody().getErrorCode());
    }

    @Test
    @Order(value = 5)
    void testCreateStock() {
        ApiStockRequest apiStockRequest = getApiStockRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ApiStockResponse> response = restTemplate.exchange("/stock/", HttpMethod.POST, new HttpEntity<>(apiStockRequest, headers), ApiStockResponse.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(apiStockRequest.getName(), response.getBody().getName());
        Assertions.assertEquals(apiStockRequest.getDescription(), response.getBody().getDescription());
        Assertions.assertEquals(apiStockRequest.getCurrentPrice(), response.getBody().getCurrentPrice());
    }

    @Test
    @Order(value = 6)
    void testCreateStock_ShouldFail_WhenProductCodeIsAlreadyExists() {
        ApiStockRequest apiStockRequest = getApiStockRequest2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<BaseException> response = restTemplate.exchange("/stock/", HttpMethod.POST, new HttpEntity<>(apiStockRequest, headers), BaseException.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        Assertions.assertEquals(StockExchangeServiceErrorCode.ALREADY_EXISTS, response.getBody().getErrorCode());
    }

    @Test
    @Order(value = 7)
    void testDeleteStock() {
        ResponseEntity<String> response = restTemplate.exchange("/stock/3", HttpMethod.DELETE, null, String.class);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Stock deleted successfully.", response.getBody());
    }

    @Test
    @Order(value = 8)
    void testDeleteStock_ShouldFail_WhenStockIsNotFound() {
        ResponseEntity<BaseException> response = restTemplate.exchange("/stock/1", HttpMethod.DELETE, null, BaseException.class);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        Assertions.assertEquals(StockExchangeServiceErrorCode.STOCK_NOT_FOUND, response.getBody().getErrorCode());
    }
}
