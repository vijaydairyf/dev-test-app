package com.devapp.devmain.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class SocietyUserEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String salutation;
    public String firstName;
    public String lastName;
    public String email;
    public String mobile;
    public String username;
    public String password;
    public boolean enabled;
    public ArrayList<String> assignedRoles;
    public ArrayList<String> assignedSocieties;

}
