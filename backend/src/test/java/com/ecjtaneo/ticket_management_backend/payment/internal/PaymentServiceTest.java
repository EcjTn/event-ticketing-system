package com.ecjtaneo.ticket_management_backend.payment.internal;

import com.ecjtaneo.ticket_management_backend.payment.internal.dto.PaymentResponse;
import com.ecjtaneo.ticket_management_backend.payment.internal.model.Payment;
import com.ecjtaneo.ticket_management_backend.payment.internal.model.PaymentStatus;
import com.ecjtaneo.ticket_management_backend.shared.events.OrderCreatedEvent;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ResourceNotFoundException;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ValidationException;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentProcessor paymentProcessor;

    private StripeClient stripeClient;

    @Mock
    private PaymentIntent paymentIntent;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        stripeClient = mock(StripeClient.class, RETURNS_DEEP_STUBS);
        paymentService = new PaymentService(paymentRepository, paymentProcessor, stripeClient, eventPublisher);
        ReflectionTestUtils.setField(paymentService, "webhookSecret", "whsec_test");
    }

    @Test
    void getPaymentInfoByOrderIdAndValid_Success() throws StripeException {
        Long orderId = 1L;
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setClientSecret("secret_123");
        payment.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentInfoByOrderIdAndValid(orderId);

        assertThat(response.clientSecret()).isEqualTo("secret_123");
        assertThat(response.status()).isEqualTo(PaymentStatus.PENDING);
        verify(paymentRepository).findByOrderId(orderId);
    }

    @Test
    void getPaymentInfoByOrderIdAndValid_NotFound_ThrowsException() {
        Long orderId = 1L;
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentInfoByOrderIdAndValid(orderId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payment not found for order id: 1");
    }

    @Test
    void getPaymentInfoByOrderIdAndValid_NotPending_ThrowsException() {
        Long orderId = 1L;
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.getPaymentInfoByOrderIdAndValid(orderId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Only pending payments can be processed");
    }

    @Test
    void getPaymentInfoByOrderIdAndValid_Expired_CancelsStripePayment() throws StripeException {
        Long orderId = 1L;
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentIntentId("pi_123");
        payment.setExpiresAt(LocalDateTime.now().minusHours(1));

        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(payment));
        when(stripeClient.v1().paymentIntents().cancel("pi_123")).thenReturn(paymentIntent);

        assertThatThrownBy(() -> paymentService.getPaymentInfoByOrderIdAndValid(orderId))
                .isInstanceOf(PaymentExpiredException.class)
                .hasMessageContaining("Payment has expired and is now cancelled");

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        verify(stripeClient.v1().paymentIntents()).cancel("pi_123");
    }

    @Test
    void cancelPaymentByOrderIdOnOrderCancelled_Success() throws StripeException {
        Long orderId = 1L;
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentIntentId("pi_123");

        when(paymentRepository.findByOrderIdForUpdate(orderId)).thenReturn(Optional.of(payment));
        when(stripeClient.v1().paymentIntents().cancel("pi_123")).thenReturn(paymentIntent);

        paymentService.cancelPaymentByOrderIdOnOrderCancelled(orderId);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
        verify(stripeClient.v1().paymentIntents()).cancel("pi_123");
    }

    @Test
    void cancelPaymentByOrderIdOnOrderCancelled_NotFound_ThrowsException() {
        Long orderId = 1L;
        when(paymentRepository.findByOrderIdForUpdate(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.cancelPaymentByOrderIdOnOrderCancelled(orderId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payment not found for order id: 1");
    }

    @Test
    void cancelPaymentByOrderIdOnOrderCancelled_NotPending_ThrowsException() {
        Long orderId = 1L;
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByOrderIdForUpdate(orderId)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.cancelPaymentByOrderIdOnOrderCancelled(orderId))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Only pending payments can be cancelled");
    }

    @Test
    void createPaymentOnOrderCreated_Success() throws StripeException {
        OrderCreatedEvent event = new OrderCreatedEvent(1L, 2L, BigDecimal.valueOf(100), LocalDateTime.now().plusMinutes(15));

        when(stripeClient.v1().paymentIntents().create(any(PaymentIntentCreateParams.class), any(RequestOptions.class))).thenReturn(paymentIntent);
        when(paymentIntent.getId()).thenReturn("pi_123");
        when(paymentIntent.getClientSecret()).thenReturn("secret_123");

        paymentService.createPaymentOnOrderCreated(event);

        verify(paymentRepository).save(any(Payment.class));
        verify(stripeClient.v1().paymentIntents()).create(any(PaymentIntentCreateParams.class), any(RequestOptions.class));
    }

    @Test
    void cancelPaymentsOnOrdersBatchExpired_Success() throws StripeException {
        List<Long> orderIds = List.of(1L, 2L);
        when(paymentRepository.bulkCancelAndReturnIntentIds(orderIds)).thenReturn(List.of("pi_1", "pi_2"));

        paymentService.cancelPaymentsOnOrdersBatchExpired(orderIds);

        verify(stripeClient.v1().paymentIntents()).cancel("pi_1");
        verify(stripeClient.v1().paymentIntents()).cancel("pi_2");
    }

    @Test
    void cancelPaymentsOnOrdersBatchExpired_EmptyList_ReturnsEarly() throws StripeException {
        paymentService.cancelPaymentsOnOrdersBatchExpired(Collections.emptyList());
        verifyNoInteractions(stripeClient.v1().paymentIntents());
    }
}
