package org.poo.Commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.entities.UserRepo;

public class SpendingReport implements Command {
    private int startTimestamp;
    private int endTimestamp;
    private String accountIBAN;
    private final int timestamp;
    private UserRepo userRepo;

    public SpendingReport(int startTimestamp, int endTimestamp, String accountIBAN, int timestamp, UserRepo userRepo) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.accountIBAN = accountIBAN;
        this.timestamp = timestamp;
        this.userRepo = userRepo;
    }

    @Override
    public void execute(ArrayNode output) {
        return;
    }
}
