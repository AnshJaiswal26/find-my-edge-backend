package com.example.find_my_edge.trade_import.exception;

public class ImportedTradeNotFoundException extends ImportedTradeException {
    public ImportedTradeNotFoundException(Long id) {
        super("Imported Trade not found with id " + id);
    }
}
