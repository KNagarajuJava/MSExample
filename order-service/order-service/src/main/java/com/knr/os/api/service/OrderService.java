package com.knr.os.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knr.os.api.common.TransactionRequest;
import com.knr.os.api.common.TransactionResponse;
import com.knr.os.api.entity.Order;
import com.knr.os.api.repository.OrderRepository;
import com.knt.os.api.common.Payment;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class OrderService {

	Logger logger = LoggerFactory.getLogger(OrderService.class);
	@Autowired
	private OrderRepository repository;
	@Autowired
	@Lazy
	private RestTemplate template;

	@Value("${microservice.payment-service.endpoints.endpoint.uri}")
	private String ENDPOINT_URL;

	public TransactionResponse saveOrder(TransactionRequest request) throws JsonProcessingException {
		String response = "";
		Order order = request.getOrder();
		Payment payment = request.getPayment();
		payment.setOrderId(order.getId());
		payment.setAmount(order.getPrice());
		// rest call
		logger.info("Order-Service Request : " + new ObjectMapper().writeValueAsString(request));
		Payment paymentResponse = template.postForObject(ENDPOINT_URL, payment, Payment.class);

		response = paymentResponse.getPaymentStatus().equals("success")
				? "payment processing successful and order placed"
				: "there is a failure in payment api , order added to cart";
		logger.info("Order Service getting Response from Payment-Service : "
				+ new ObjectMapper().writeValueAsString(response));
		repository.save(order);
		return new TransactionResponse(order, paymentResponse.getAmount(), paymentResponse.getTransactionId(),
				response);
	}

	@CircuitBreaker(name = "${spring.application.name}", fallbackMethod = "getDefaultResponse")
	public String findOrderHistoryByOrderId(int orderId) {
		logger.info("Order-Service :findOrderHistoryByOrderId method ");
		logger.info("http://localhost:9191/payment/" + orderId);
		ResponseEntity<Payment> payemntresponse = template.getForEntity("http://localhost:9191/payment/" + orderId,Payment.class);
		
		return "successfull fetched the order details "+payemntresponse;
	}
	public String getDefaultResponse(int orderId, Exception ex) {
		logger.info("Order-Service :getDefaultResponse method ");
		
		return "Facing some issues we are on it ....please try after some time";
	}
}
