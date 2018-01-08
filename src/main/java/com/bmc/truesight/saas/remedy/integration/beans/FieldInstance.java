package com.bmc.truesight.saas.remedy.integration.beans;

public enum FieldInstance {

    CharacterField("CharacterField"), IntegerField("IntegerField"), DecimalField("DecimalField"), RealField(
            "RealField"), DiaryField("DiaryField"), SelectionField("SelectionField"), AttachmentField(
            "AttachmentField"), CurrencyField("CurrencyField"), DateOnlyField("DateOnlyField"), TimeOnlyField(
            "TimeOnlyField"), DateTimeField("DateTimeField"), UnknownField("UnknownField");

    private final String text;

    private FieldInstance(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
