package io.littlehorse.examples;

import io.littlehorse.sdk.worker.LHTaskMethod;

public final class ExampleTasks {

    public static final String SEND_EMAIL_TASK = "send-customer-email";
    public static final String SEND_SMS_TASK = "send-customer-sms";
    public static final String AUTHORIZE_PAYMENT_TASK = "authorize-payment";
    public static final String RECORD_PAYMENT_ISSUE_TASK = "record-payment-issue";
    public static final String CONFIRM_PAYMENT_TASK = "confirm-payment";
    public static final String CHECK_INVENTORY_TASK = "check-inventory";
    public static final String SHIP_ITEM_TASK = "ship-item";
    public static final String CANCEL_ORDER_TASK = "cancel-order";

    private static final String INVALID_PAYMENT_METHOD = "invalid";
    private static final String OUT_OF_STOCK = "out-of-stock";

    @LHTaskMethod(SEND_EMAIL_TASK)
    public void sendEmail(String customerId, String message) {
        System.out.println("Email to " + customerId + ": " + message);
    }

    @LHTaskMethod(SEND_SMS_TASK)
    public void sendSms(String customerId, String message) {
        System.out.println("SMS to " + customerId + ": " + message);
    }

    @LHTaskMethod(AUTHORIZE_PAYMENT_TASK)
    public boolean authorizePayment(String paymentMethod, Long amount) {
        boolean authorized = !INVALID_PAYMENT_METHOD.equals(paymentMethod);
        System.out.println("Payment authorization for amount=" + amount + ": " + authorized);
        return authorized;
    }

    @LHTaskMethod(RECORD_PAYMENT_ISSUE_TASK)
    public void recordPaymentIssue(String customerId, boolean customerResponded) {
        System.out.println("Recorded payment issue for " + customerId
                + "; customer responded=" + customerResponded);
    }

    @LHTaskMethod(CONFIRM_PAYMENT_TASK)
    public void confirmPayment(Long amount) {
        System.out.println("Confirmed payment amount=" + amount);
    }

    @LHTaskMethod(CHECK_INVENTORY_TASK)
    public boolean checkInventory(String item) {
        boolean available = !OUT_OF_STOCK.equals(item);
        System.out.println("Inventory check for " + item + ": " + available);
        return available;
    }

    @LHTaskMethod(SHIP_ITEM_TASK)
    public void shipItem(String orderId, String item) {
        System.out.println("Shipped item " + item + " for order " + orderId);
    }

    @LHTaskMethod(CANCEL_ORDER_TASK)
    public void cancelOrder(String orderId, String reason) {
        System.out.println("Cancelled order " + orderId + "; reason=" + reason);
    }
}
