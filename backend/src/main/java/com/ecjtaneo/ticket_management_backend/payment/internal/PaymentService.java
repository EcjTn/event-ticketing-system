package com.ecjtaneo.ticket_management_backend.payment.internal;

import com.ecjtaneo.ticket_management_backend.payment.internal.model.Payment;
import com.ecjtaneo.ticket_management_backend.payment.internal.model.PaymentStatus;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ResourceNotFoundException;
import com.ecjtaneo.ticket_management_backend.shared.exceptions.ValidationException;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StripeClient stripeClient;

    //TODO: use specific exceptions instead of generic ones and handle them in the module's controller advice.
    PaymentResponseDto getPaymentInfoByOrderIdAndValid(Long orderId) throws StripeException {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));

        if(payment.getStatus() != PaymentStatus.PENDING) throw new ValidationException("Only pending payments can be processed");
        if(payment.getExpiresAt().isBefore(LocalDateTime.now())) {
            stripeClient.v1().paymentIntents().cancel(payment.getPaymentIntentId());
            payment.setStatus(PaymentStatus.CANCELLED);

            throw new ValidationException("Payment has expired and is now cancelled");
        }

        return new PaymentResponseDto(payment.getClientSecret(), payment.getStatus());
    }

    // Event handler for OrderCancelledEvent should call this method to cancel the payment intent.
    @Transactional
    public void cancelPaymentByOrderId(Long orderId) throws StripeException {
        Payment payment = paymentRepository.findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));

        if(payment.getStatus() != PaymentStatus.PENDING) throw new ValidationException("Only pending payments can be cancelled");

        stripeClient.v1().paymentIntents().cancel(payment.getPaymentIntentId());
        payment.setStatus(PaymentStatus.CANCELLED);
    }

}
