package abax;

import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class TransactionGenerator {

    Logger logger = org.slf4j.LoggerFactory.getLogger(TransactionGenerator.class);

    private final TransactionGateway transactionGateway;
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(16);

    @Value("${floader.max-transactions}")
    private int maxTransactions;

    @Value("${floader.accounts-count}")
    private int accountsCount;

    @Value("${floader.iban-prefix}")
    private String ibanPrefix;

    @Value("${floader.cooldown-seconds:10}")
    private int cooldownSeconds;

    private final ConfigurableApplicationContext context;


    private String[] ibans;
    private CountDownLatch latch;

    public TransactionGenerator(TransactionGateway transactionGateway, ConfigurableApplicationContext context) {
        this.transactionGateway = transactionGateway;
        this.context = context;
    }

    @PostConstruct
    public void init() {
        ibans = new String[accountsCount];
        IntStream.range(0, accountsCount)
                .forEach(i -> ibans[i] = ibanPrefix + Math.round(1000000 + Math.random() * 8000000));

        latch = new CountDownLatch(maxTransactions);
    }

    @Scheduled(fixedDelay = 10000)
    public void checkForShutdown() throws InterruptedException {
        if (latch.getCount() <= 0) {
            logger.warn("All transactions generated, shutting down in {} sec...",cooldownSeconds);
            Thread.sleep(cooldownSeconds * 1000);
            logger.warn("Shutdown now");

            context.close();
            System.exit(0);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        logger.warn("Generating {} transactions {}", maxTransactions);
        IntStream.range(0, maxTransactions).forEach(i -> executor.execute(() -> {
            // String transaction = "Transaction ID: " + UUID.randomUUID();
            Transaction transaction = new Transaction(UUID.randomUUID().toString(), transactionType(), iban(), amount(),
                    currency(),
                    remittenceInfo());
            logger.debug("Generated {}", transaction);
            transactionGateway.send(transaction);
            latch.countDown();

        }));
    }

    private String remittenceInfo() {
        String[] randomEnglishWords = { "apple", "banana", "cherry", "date", "elderberry", "fig", "grape", "honeydew",
                "kiwi", "lemon", "mango", "nectarine", "orange", "pear", "quince", "raspberry", "strawberry",
                "tangerine",
                "watermelon" };

        // random remittence info 3 words
        return randomEnglishWords[(int) (Math.random() * randomEnglishWords.length)] + " "
                + randomEnglishWords[(int) (Math.random() * randomEnglishWords.length)] + " "
                + randomEnglishWords[(int) (Math.random() * randomEnglishWords.length)];
    }

    private String currency() {
        String[] currencies = { "USD", "EUR", "GBP", "JPY", "CNY" };
        return currencies[(int) (Math.random() * currencies.length)];
    }

    private double amount() {
        // round random to 2 decimal places
        return Math.round(Math.random() * 1000000) / 100.0;
    }

    private String transactionType() {
        String[] types = { "CREDIT", "DEBIT" };
        return types[(int) (Math.random() * types.length)];
    }

    private String iban() {
        return ibans[(int) (Math.random() * ibans.length)];
    }

}

@MessagingGateway
interface TransactionGateway {
    @Gateway(requestChannel = "transactionChannel")
    void send(Transaction message);
}
