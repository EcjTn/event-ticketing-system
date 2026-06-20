package com.ecjtaneo.ticket_management_backend.payment.internal;

import com.ecjtaneo.ticket_management_backend.payment.internal.model.Payment;
import com.ecjtaneo.ticket_management_backend.payment.internal.model.PaymentStatus;
import com.ecjtaneo.ticket_management_backend.shared.events.PaymentFailedEvent;
import com.ecjtaneo.ticket_management_backend.shared.events.PaymentSucceededEvent;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ResourceNotFoundException;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.param.RefundCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentProcessorTest {

    @Mock
    private PaymentRepository paymentRepository;

    private StripeClient stripeClient;

    @Mock
    private Refund refund;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private PaymentProcessor paymentProcessor;

    @BeforeEach
    void setUp() {
        stripeClient = mock(StripeClient.class, RETURNS_DEEP_STUBS);
        paymentProcessor = new PaymentProcessor(paymentRepository, stripeClient, eventPublisher);
    }

    @Test
    void processPaymentSuccess_PendingStatus_Success() throws StripeException {
        String paymentIntentId = "pi_123";
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setOrderId(1L);

        when(paymentRepository.findByPaymentIntentIdForUpdate(paymentIntentId)).thenReturn(Optional.of(payment));

        paymentProcessor.processPaymentSuccess(paymentIntentId);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(payment.getPaidAt()).isNotNull();

        verify(eventPublisher).publishEvent(any(PaymentSucceededEvent.class));
    }

    @Test
    void processPaymentSuccess_AlreadyCancelled_RefundsStripe() throws StripeException {
        String paymentIntentId = "pi_123";
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setOrderId(1L);

        when(paymentRepository.findByPaymentIntentIdForUpdate(paymentIntentId)).thenReturn(Optional.of(payment));
        when(stripeClient.v1().refunds().create(any(RefundCreateParams.class))).thenReturn(refund);

        paymentProcessor.processPaymentSuccess(paymentIntentId);

        verify(stripeClient.v1().refunds()).create(any(RefundCreateParams.class));
        verify(paymentRepository, never()).save(payment);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void processPaymentSuccess_AlreadyProcessed_Skips() throws StripeException {
        String paymentIntentId = "pi_123";
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setOrderId(1L);

        when(paymentRepository.findByPaymentIntentIdForUpdate(paymentIntentId)).thenReturn(Optional.of(payment));

        paymentProcessor.processPaymentSuccess(paymentIntentId);

        verify(paymentRepository, never()).save(any(Payment.class));
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void processPaymentSuccess_NotFound_ThrowsException() {
        String paymentIntentId = "pi_nonexistent";
        when(paymentRepository.findByPaymentIntentIdForUpdate(paymentIntentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentProcessor.processPaymentSuccess(paymentIntentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payment not found for payment intent ID: pi_nonexistent");
    }

    @Test
    void processPaymentFailure_PendingStatus_Success() throws StripeException {
        String paymentIntentId = "pi_123";
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.PENDING);
        payment.setOrderId(1L);

        when(paymentRepository.findByPaymentIntentIdForUpdate(paymentIntentId)).thenReturn(Optional.of(payment));

        paymentProcessor.processPaymentFailure(paymentIntentId);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);

        verify(eventPublisher).publishEvent(any(PaymentFailedEvent.class));
    }

    @Test
    void processPaymentFailure_AlreadyCancelled_NoAction() throws StripeException {
        String paymentIntentId = "pi_123";
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setOrderId(1L);

        when(paymentRepository.findByPaymentIntentIdForUpdate(paymentIntentId)).thenReturn(Optional.of(payment));

        paymentProcessor.processPaymentFailure(paymentIntentId);

        verify(paymentRepository, never()).save(any(Payment.class));
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void processPaymentFailure_AlreadyProcessed_Skips() throws StripeException {
        String paymentIntentId = "pi_123";
        Payment payment = new Payment();
        payment.setStatus(PaymentStatus.FAILED);
        payment.setOrderId(1L);

        when(paymentRepository.findByPaymentIntentIdForUpdate(paymentIntentId)).thenReturn(Optional.of(payment));

        paymentProcessor.processPaymentFailure(paymentIntentId);

        verify(paymentRepository, never()).save(any(Payment.class));
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void processPaymentFailure_NotFound_ThrowsException() {
        String paymentIntentId = "pi_nonexistent";
        when(paymentRepository.findByPaymentIntentIdForUpdate(paymentIntentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentProcessor.processPaymentFailure(paymentIntentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Payment not found for payment intent ID: pi_nonexistent");
    }
}
