package ru.antoncharov.socks.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "socks")
@Getter
@Setter
public class Socks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private int cottonPercentage;

    @Column(nullable = false)
    private int quantity;
}
