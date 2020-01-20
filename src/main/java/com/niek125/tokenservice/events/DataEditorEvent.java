package com.niek125.tokenservice.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class DataEditorEvent {
    private String creator;

    public DataEditorEvent() {
        this.creator = "token-service";
    }
}
