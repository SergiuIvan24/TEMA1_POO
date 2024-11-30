package org.poo.Commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.entities.Account;
import org.poo.entities.User;
import org.poo.entities.UserRepo;

public class DeleteAccount implements Command {
    private final String accountIBAN;
    private final int timestamp;
    private final String email;
    private final UserRepo userRepo;

    public DeleteAccount(UserRepo userRepo, String email, String accountIBAN, int timestamp) {
        this.userRepo = userRepo;
        this.email = email;
        this.accountIBAN = accountIBAN;
        this.timestamp = timestamp;
    }

    @Override
    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = userRepo.getUser(email);
        if (user == null) {
            ObjectNode errorNode = objectMapper.createObjectNode();
            errorNode.put("error", "User not found for email: " + email);
            errorNode.put("timestamp", timestamp);
            output.add(errorNode);
            return;
        }

        boolean deleted = userRepo.deleteAccount(email, accountIBAN);

        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.put("command", "deleteAccount");
        if (deleted) {
            ObjectNode successNode = objectMapper.createObjectNode();
            successNode.put("success", "Account deleted");
            successNode.put("timestamp", timestamp);
            resultNode.set("output", successNode);
        } else {
            ObjectNode errorNode = objectMapper.createObjectNode();
            errorNode.put("error", "Account couldn't be deleted - see org.poo.transactions for details");
            errorNode.put("timestamp", timestamp);
            resultNode.set("output", errorNode);
            }
        resultNode.put("timestamp", timestamp);
        output.add(resultNode);
    }



}
