package com.vitrum.api.data.submodels;

import com.vitrum.api.data.models.User;
import com.vitrum.api.data.enums.TokenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tokens")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    @Id
    private String id;

    private String token;

    private TokenType tokenType = TokenType.BEARER;

    private boolean revoked;

    private boolean expired;

    @DBRef
    private User user;
}
