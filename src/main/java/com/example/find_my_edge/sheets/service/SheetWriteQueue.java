package com.example.find_my_edge.sheets.service;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SheetWriteQueue {

    private final ExecutorService sheetExecutor =
            Executors.newSingleThreadExecutor();

    public void submit(Runnable task) {
        sheetExecutor.submit(task);
    }

    @PreDestroy
    public void shutdown() {
        sheetExecutor.shutdown();
    }
}
