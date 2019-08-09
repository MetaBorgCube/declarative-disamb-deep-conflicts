package org.spoofax.jsglr2.parseforest;

public enum ParseForestRepresentation {
    Null, Basic, Hybrid, DataDependent, LayoutSensitive, Incremental;

    public static ParseForestRepresentation standard() {
        return Hybrid;
    }
}
