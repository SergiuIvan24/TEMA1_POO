package org.poo.Commands;

import com.fasterxml.jackson.databind.node.ArrayNode;

public interface Command {
    void execute(ArrayNode output);
}