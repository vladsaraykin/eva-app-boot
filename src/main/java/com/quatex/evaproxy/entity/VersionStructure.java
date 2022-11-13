package com.quatex.evaproxy.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class VersionStructure<T> {

    private T currentValue;
    private T newValue;
}
