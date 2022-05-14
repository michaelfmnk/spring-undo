package dev.fomenko.springundocore.service;

import java.util.UUID;

public class GuidActionIdGenerator implements ActionIdGenerator {
    @Override
    public String generateId() {
        return UUID.randomUUID().toString();
    }
}
