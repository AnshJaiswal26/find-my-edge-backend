package com.example.find_my_edge.trade.service;

import java.time.LocalDate;

public interface TradeSyncService {

    void fullSync(String broker);

    void incrementalSync(String broker);

    void customSync(String broker, LocalDate fromDate, LocalDate toDate);

}
