package com.lh;

import lombok.*;

import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class Hello implements Serializable {
    private String message;
    private String description;
}
