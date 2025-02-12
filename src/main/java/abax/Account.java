package abax;

import lombok.Data;

@Data
public class Account {
    private final String iban;
    private final String currency;
}
