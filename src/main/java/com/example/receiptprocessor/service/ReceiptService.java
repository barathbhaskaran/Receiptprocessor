package com.example.receiptprocessor.service;

import com.example.receiptprocessor.model.Item;
import com.example.receiptprocessor.model.Receipt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ReceiptService {

    private final Map<String, Integer> receiptPoints = new HashMap<>();

    public String processReceipt(Receipt receipt) {
        String id = UUID.randomUUID().toString();
        int points = calculatePoints(receipt);
        receiptPoints.put(id, points);
        return id;
    }

    public Integer getPoints(String id) {
        return receiptPoints.get(id);
    }

    private int calculatePoints(Receipt receipt) {
        int points = 0;

        // 1. One point for every alphanumeric character in the retailer name.
        points += receipt.getRetailer().replaceAll("[^a-zA-Z0-9]", "").length();

        // 2. 50 points if the total is a round dollar amount with no cents.
        if (receipt.getTotal().endsWith(".00")) {
            points += 50;
        }

        // 3. 25 points if the total is a multiple of 0.25.
        if (Double.parseDouble(receipt.getTotal()) % 0.25 == 0) {
            points += 25;
        }

        // 4. 5 points for every two items on the receipt.
        points += (receipt.getItems().size() / 2) * 5;

        // 5. Points based on item description length and price.
        for (Item item : receipt.getItems()) {
            int descriptionLength = item.getShortDescription().trim().length();
            if (descriptionLength % 3 == 0) {
                double price = Double.parseDouble(item.getPrice());
                points += Math.ceil(price * 0.2);
            }
        }

        // 6. 6 points if the day in the purchase date is odd.
        LocalDate date = LocalDate.parse(receipt.getPurchaseDate());
        if (date.getDayOfMonth() % 2 != 0) {
            points += 6;
        }

        // 7. 10 points if the time of purchase is after 2:00pm and before 4:00pm.
        LocalTime time = LocalTime.parse(receipt.getPurchaseTime());
        if (time.isAfter(LocalTime.of(14, 0)) && time.isBefore(LocalTime.of(16, 0))) {
            points += 10;
        }

        return points;
    }
}
