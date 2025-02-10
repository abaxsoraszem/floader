package abax;

import lombok.Data;


@Data
public class Transaction {
    private final String transactionId;
    private final String transactionType;
    private final String iban;
    private final double amount;
    private final String currency;
    private final String remittenceInfo;
}
