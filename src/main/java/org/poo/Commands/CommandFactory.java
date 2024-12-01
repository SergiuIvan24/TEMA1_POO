package org.poo.Commands;

import com.fasterxml.jackson.databind.JsonNode;
import org.poo.entities.UserRepo;

import java.util.ArrayList;
import java.util.List;

public class CommandFactory {
    private final UserRepo userRepo;

    public CommandFactory(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    private List<String> getAccountsList(JsonNode accountsNode) {
        List<String> accounts = new ArrayList<>();
        for (JsonNode accountNode : accountsNode) {
            accounts.add(accountNode.asText());
        }
        return accounts;
    }

    public Command createCommand(String commandType, JsonNode commandData) {
        switch (commandType) {
            case "addAccount":
                Double interestRate = commandData.has("interestRate") && !commandData.get("interestRate").isNull()
                        ? commandData.get("interestRate").asDouble()
                        : null;
                return new AddAccount(
                        userRepo,
                        commandData.get("email").asText(),
                        commandData.get("currency").asText(),
                        commandData.get("accountType").asText(),
                        commandData.get("timestamp").asInt(),
                        interestRate
                );
            case "createCard":
                return new CreateCard(
                        commandData.get("account").asText(),
                        commandData.get("email").asText(),
                        userRepo,
                        commandData.get("timestamp").asInt()
                );
            case "addFunds":
                return new AddFunds(
                        commandData.get("account").asText(),
                        commandData.get("amount").asDouble(),
                        userRepo,
                        commandData.get("timestamp").asInt()
                );
            case "printUsers":
                return new PrintUsers(userRepo, commandData.get("timestamp").asInt());
            case "deleteAccount":
                return new DeleteAccount(
                        userRepo,
                        commandData.get("email").asText(),
                        commandData.get("account").asText(),
                        commandData.get("timestamp").asInt()
                );
            case "createOneTimeCard":
                return new CreateOneTimeCard(
                        commandData.get("account").asText(),
                        commandData.get("email").asText(),
                        userRepo,
                        commandData.get("timestamp").asInt()
                );
            case "deleteCard":
                return new DeleteCard(
                        commandData.get("email").asText(),
                        commandData.get("cardNumber").asText(),
                        userRepo,
                        commandData.get("timestamp").asInt()
                );
            case "setMinimumBalance":
                return new SetMinimumBalance(
                        commandData.get("account").asText(),
                        commandData.get("amount").asDouble(),
                        userRepo,
                        commandData.get("timestamp").asInt()
                );
            case "payOnline":
                return new PayOnline(
                        commandData.get("cardNumber").asText(),
                        commandData.get("amount").asDouble(),
                        commandData.get("currency").asText(),
                        commandData.get("timestamp").asInt(),
                        commandData.get("description").asText(),
                        commandData.get("commerciant").asText(),
                        commandData.get("email").asText(),
                        userRepo
                );
            case "sendMoney":
                return new SendMoney(
                        commandData.get("account").asText(),
                        commandData.get("receiver").asText(),
                        commandData.get("amount").asDouble(),
                        commandData.get("timestamp").asInt(),
                        commandData.get("email").asText(),
                        commandData.get("description").asText(),
                        userRepo
                );
            case "printTransactions":
                return new PrintTransactions(
                        commandData.get("email").asText(),
                        commandData.get("timestamp").asInt(),
                        userRepo
                );
            case "setAlias":
                return new SetAlias(
                        commandData.get("email").asText(),
                        commandData.get("account").asText(),
                        commandData.get("alias").asText(),
                        commandData.get("timestamp").asInt(),
                        userRepo
                );
            case "checkCardStatus":
                return new CheckCardStatus(
                        commandData.get("cardNumber").asText(),
                        commandData.get("timestamp").asInt(),
                        userRepo
                );
            case "changeInterestRate":
                return new ChangeInterestRate(
                        commandData.get("account").asText(),
                        commandData.get("interestRate").asDouble(),
                        commandData.get("timestamp").asInt(),
                        userRepo
                );
            case "splitPayment":
                return new SplitPayment(
                        getAccountsList(commandData.get("accounts")),
                        commandData.get("amount").asDouble(),
                        commandData.get("currency").asText(),
                        commandData.get("timestamp").asInt(),
                        userRepo
                );

            default:
                throw new IllegalArgumentException("Invalid command: " + commandType);
        }
    }
}
