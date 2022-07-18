package com.unla.agroecologiaiot.models.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProfileDTO {

    public String username;
    public String name;
    public String surname;
    public String roleCode;

}
