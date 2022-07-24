package com.unla.agroecologiaiot.models.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class ProfileDTO {

    public String username;
    public String name;
    public String surname;
    public String roleCode;

}
