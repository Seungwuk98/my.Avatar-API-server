package avatar.apiserver.inferenceserver;

/**
 * InferenceServerInterface
 */
public interface InferenceServerInterface {

    InferenceResult inferenceHead(String imageUrl, String styleCode);
    CombineAvatarResult combineAvatar(String headUrl, int bodyNum, int hairNum);
}