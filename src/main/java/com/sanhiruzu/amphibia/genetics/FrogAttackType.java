package com.sanhiruzu.amphibia.genetics;

public enum FrogAttackType {
    NONE("Cannot attack"),
    BITE("Melee bite attack"),
    TONGUE("Ranged tongue attack"),
    BOTH("Melee and ranged attacks");

    private final String description;

    FrogAttackType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
