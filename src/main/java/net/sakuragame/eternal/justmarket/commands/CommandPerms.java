package net.sakuragame.eternal.justmarket.commands;

import lombok.Getter;

public enum CommandPerms {

    USER("justmarket.user"),
    ADMIN("justmarket.admin");

    @Getter
    private final String node;

    CommandPerms(String node) {
        this.node = node;
    }
}