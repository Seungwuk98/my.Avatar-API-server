package avatar.apiserver.preprocess;

import java.io.IOException;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class OpenCVPreprocesser implements Preprocesser{

    private final PreprocessDir preprocessDir;

    public PreprocessResult process(String photoUrl){
        PreprocessResult result = new PreprocessResult();
        ProcessBuilder builder = new ProcessBuilder(preprocessDir.getPythonDir(), 
                                                    preprocessDir.getPreprocessorDir(),
                                                    "-b", preprocessDir.getBeforeDir(),
                                                    "-a", preprocessDir.getAfterDir(),
                                                    "-i", photoUrl);
        int exitVal;
        try {
            Process process = builder.start();
            exitVal = process.waitFor();
        } catch (IOException e) {
            // TODO exception 만들것
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            // TODO exception 만들것
            throw new RuntimeException(e);
        }

        log.info("Python excuted, exitVal : {}", exitVal);
        result.setResult(exitVal);
        return result;
    }
  
}
