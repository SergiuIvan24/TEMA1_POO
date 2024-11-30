package org.poo.Commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.entities.UserRepo;

public class SetAlias implements Command {
    private String email;
    private String accountIBAN;
    private String alias;
    private final int timestamp;
    private UserRepo userRepo;

    public SetAlias(String email, String accountIBAN, String alias, int timestamp, UserRepo userRepo) {
        this.email = email;
        this.accountIBAN = accountIBAN;
        this.alias = alias;
        this.timestamp = timestamp;
        this.userRepo = userRepo;
    }

    @Override
    public void execute(ArrayNode output) {
        userRepo.getUser(email).setAlias(alias, accountIBAN);
    }
}
