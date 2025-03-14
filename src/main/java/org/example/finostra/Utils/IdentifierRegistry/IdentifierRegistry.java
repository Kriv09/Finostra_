package org.example.finostra.Utils.IdentifierRegistry;

import org.example.finostra.Entity.User.UserInfo.UserInfo;

import java.util.Objects;

public class IdentifierRegistry {
    public String For(Object o)
    {
        if(o instanceof UserInfo info)
            return Encryption.encryptSHA256(
                    info.getPhoneNumber() + Objects.hash(o)
            );
        else
            return String.valueOf(Objects.hash(o));

    }
}
