package com.onesandzeros;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.onesandzeros.model.register.LoginPayload;
import com.onesandzeros.models.AccountType;
import com.onesandzeros.models.BaseResponse;

//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SsoServerApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	// @Test
	@SuppressWarnings("unchecked")
	public void contextLoads() {
		LoginPayload payload = new LoginPayload();
		payload.setAccountType(AccountType.EMAIL);
		payload.setEmail("sharana.ayippa@gmail.com");
		payload.setPassword("sharana");
		HttpEntity<LoginPayload> httpEnt = new HttpEntity<>(payload, getDefaultHeaders());

		BaseResponse<String> resp = this.restTemplate.postForObject("/login", httpEnt, BaseResponse.class);
		assertThat(resp.getCode()).isEqualTo(HttpStatus.OK.value());
	}

	private HttpHeaders getDefaultHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "*/*");
		return headers;
	}

}
