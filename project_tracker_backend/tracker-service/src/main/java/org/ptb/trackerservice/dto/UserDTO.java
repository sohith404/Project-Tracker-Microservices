package org.ptb.trackerservice.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer userId;
    private String name;
    private String email;
    private String role;
}
