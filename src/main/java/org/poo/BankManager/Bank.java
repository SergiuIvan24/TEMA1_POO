package org.poo.BankManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.Commands.Command;
import org.poo.Commands.CommandFactory;
import org.poo.entities.User;
import org.poo.entities.UserRepo;
import org.poo.utils.Utils;

public class Bank {
    private final UserRepo userRepo;
    private final CommandFactory commandFactory;
    private final ArrayNode output;
    public Bank(JsonNode inputData) {
        Utils.resetRandom();
        ObjectMapper objectMapper = new ObjectMapper();
        this.userRepo = new UserRepo();
        this.commandFactory = new CommandFactory(userRepo);
        this.output = objectMapper.createArrayNode();

        for (JsonNode userNode : inputData.get("users")) {
            String firstName = userNode.get("firstName").asText();
            String lastName = userNode.get("lastName").asText();
            String email = userNode.get("email").asText();
            userRepo.addUser(new User(firstName, lastName, email));
        }

        for (JsonNode rateNode : inputData.get("exchangeRates")) {
            String from = rateNode.get("from").asText();
            String to = rateNode.get("to").asText();
            double rate = rateNode.get("rate").asDouble();

            if (rate <= 0) {
                throw new IllegalArgumentException("Invalid exchange rate: " + rate);
            }

            userRepo.addExchangeRate(from, to, rate);
        }
    }

    public void executeCommands(JsonNode commands) {
        for (JsonNode commandData : commands) {
            String commandType = commandData.get("command").asText();
            try {
                Command command = commandFactory.createCommand(commandType, commandData);
                command.execute(output);
            } catch (IllegalArgumentException e) {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode errorNode = objectMapper.createObjectNode();
                errorNode.put("command", commandType);
                errorNode.put("timestamp", commandData.get("timestamp").asInt());
                errorNode.put("error", e.getMessage());
                output.add(errorNode);
            }
        }
    }

    public ArrayNode getOutput() {
        return output;
    }
}
