package com.sar.os.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sar.api.repository.OrderRepository;
import com.sar.os.api.common.Payment;
import com.sar.os.api.common.TransactionRequest;
import com.sar.os.api.common.TransactionResponse;
import com.sar.os.api.entity.Order;


@Service
@RefreshScope
public class OrderService {

	Logger logger = LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private OrderRepository repository;

	@Autowired
	@Lazy
    private RestTemplate template;

	// @Value("${microservice.payment-service.endpoints.endpoint.uri}")
	// private String ENDPOINT_URL;

	public TransactionResponse saveOrder(TransactionRequest request) throws JsonProcessingException {
		String response = "";
		Order order = request.getOrder();
		Payment payment = request.getPayment();
		payment.setOrderId(order.getId());
		payment.setAmount(order.getPrice());
		// rest call
		logger.info("Order-Service Request : " + new ObjectMapper().writeValueAsString(request));
		Payment paymentResponse = template.postForObject("http://localhost:9191/payment-service/payment/doPayment/",
				payment, Payment.class);
		logger.info("Order-Service Request : " + paymentResponse.getTransactionId());
		response = paymentResponse.getPaymentStatus().equals("success")
				? "payment processing successful and order placed"
				: "there is a failure in payment api , order added to cart";
		logger.info("Order Service getting Response from Payment-Service : "
				+ new ObjectMapper().writeValueAsString(response));
		repository.save(order);
		return new TransactionResponse(order, paymentResponse.getAmount(), paymentResponse.getTransactionId(),
				response);
	}
}
