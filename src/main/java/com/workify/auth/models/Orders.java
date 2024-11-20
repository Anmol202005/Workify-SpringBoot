package com.workify.auth.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Entity
@Table(name = "Orders")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long myOrderId;
    private String orderId;
    private Integer amount;
    private String receipt;
    private String status;

    @ManyToOne
    private User user;

    private String paymentId;
}
