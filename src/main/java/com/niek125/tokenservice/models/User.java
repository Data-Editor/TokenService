package com.niek125.tokenservice.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class User {
    private String userid;
    private String profilepicture;
    private String username;
    private String email;
}
