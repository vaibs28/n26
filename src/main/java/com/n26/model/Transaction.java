package com.n26.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class Transaction {

    @Getter
    @Setter
    private double amount;
    @Getter
    @Setter
    private long timestamp;
    
}
