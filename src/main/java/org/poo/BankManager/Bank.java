package org.poo.BankManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.Commands.Command;
import org.poo.Commands.CommandFactory;
import org.poo.entities.User;
import org.poo.entities.UserRepo;
import org.poo.utils.Utils;

public final class Bank {
    private final UserRepo userRepo;
    private final CommandFactory commandFactory;
    private final ArrayNode output;

    public Bank(final JsonNode inputData) {
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

            userRepo.addExchangeRate(from, to, rate);
        }
    }

    /**
     * Executa comenzile din input
     * @param commands comenzi de executat
     */
    public void executeCommands(final JsonNode commands) {
        for (JsonNode commandData : commands) {
            String commandType = commandData.get("command").asText();
                Command command = commandFactory.createCommand(commandType, commandData);
                command.execute(output);
        }
    }
    /**
     * @return output JSON-ul final
     */
    public ArrayNode getOutput() {
        return output;
    }
}
