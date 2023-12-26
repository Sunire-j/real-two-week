package com.example.realtwoweek.Util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserIdentity {
    private String email;
    private String provider;
}
