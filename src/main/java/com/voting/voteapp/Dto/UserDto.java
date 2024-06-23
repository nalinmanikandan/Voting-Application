package com.voting.voteapp.Dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String Username;

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        this.Username = username;
    }
}
