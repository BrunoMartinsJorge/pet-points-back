package br.com.api.petpoints.shared.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.function.Function;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ColunaRelatorio {
    private String header;
    private Function<Object, Object> valueExtractor;
}
