package com.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class Token {
    private final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String token;
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
    private Date expiryDate;

    public Token(User user){
        this.user = user;
        this.token = generateToken();
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }
    public Date calculateExpiryDate(int expiryTimeInMinutes){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
    public String generateToken(){
        Random rnd = new Random();
        int number = rnd.nextInt(100000, 999999);
        return String.format("%06d", number);
    }
    public boolean isExpirationValid()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        Date currentDate = new Date(cal.getTime().getTime());

        return currentDate.compareTo(expiryDate) < 0;
    }

}
