package com.niek125.tokenservice.events;

import com.niek125.tokenservice.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserLoggedInEvent extends DataEditorEvent {
    private User user;

    public UserLoggedInEvent(String origin, User user) {
        super(origin);
        this.user = user;
    }
}
