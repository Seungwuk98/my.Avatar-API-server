package avatar.apiserver.preprocess;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;


@Component
@Getter
@Setter
public class PreprocessDir {
    @Value("${preprocess.python-dir}")
    private String pythonDir;
    @Value("${preprocess.preprocessor-dir}")
    private String preprocessorDir;
    @Value("${preprocess.before-dir}")
    private String beforeDir;
    @Value("${preprocess.after-dir}")
    private String afterDir;
}
