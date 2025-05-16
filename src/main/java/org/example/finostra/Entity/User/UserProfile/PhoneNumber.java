package org.example.finostra.Entity.User.UserProfile;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "phoneNumber")
public class PhoneNumber {
    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = true, columnDefinition = "NVARCHAR(100)")
    private String description;
}
