package avatar.apiserver.converter;

import org.springframework.core.convert.converter.Converter;

import avatar.apiserver.domain.Sexuality;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SexualityConverter implements Converter<String, Sexuality>{

    @Override
    public Sexuality convert(String source) {
        log.info("convert to sexuality : {}", source);
        return Sexuality.valueOf(source.toUpperCase());
    }
    
}
