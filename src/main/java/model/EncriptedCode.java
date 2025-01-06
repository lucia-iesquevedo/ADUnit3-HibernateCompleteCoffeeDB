/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"coffee"})

@Entity
@Table(name = "encriptedcodes")
public class EncriptedCode {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id_coffee")
    private int idCoffee;

    @Column(name = "code")
    private int code;

    @OneToOne(mappedBy = "encriptedCode")
    private Coffee coffee;

    public EncriptedCode(int code) {
        this.code = code;
    }

    public void setCoffee(Coffee coffee) {
        this.coffee = coffee;
    }
}