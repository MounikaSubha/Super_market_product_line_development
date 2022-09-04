package com.supermarket;

import java.io.IOException;

public interface IPaymentGateway {

    String payUsingCredit(String cardNumber) throws IOException;

    String payUsingDebit(String cardNumber, String pin) throws IOException;

    boolean payUsingCash();

    boolean payUsingCheck();

}
