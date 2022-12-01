package avatar.apiserver.serivce;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import avatar.apiserver.controller.StatusConst;
import avatar.apiserver.domain.GpuWorkingItem;
import avatar.apiserver.domain.WorkqueueOrder;
import avatar.apiserver.inferenceserver.InferenceResult;
import avatar.apiserver.inferenceserver.InferenceServerInterface;
import avatar.apiserver.repository.DatabaseRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkQueueService {
    
    private final DatabaseRepository repository;
    private final InferenceServerInterface inferenceServerInterface;
    
    public void init() {
        setGPUStatus(0, "waiting");
        setGPUStatus(1, "waiting");
    }

    public Optional<WorkqueueOrder> getNextWorkqueueOrder() {
        return repository.getNextOrder();
    }

    public String getUserStatusInWorkqueue(String id){
        return repository.getOrderStatus(id);
    }

    public void setUserStatusInWorkqueueToWaiting(String id) {
        repository.setOrderStatusWaiting(id);
    }

    public void setUserStatusInWorkqueueToLoading(String id) {
        repository.setOrderStatusLoading(id);
    }

    public void setUserStatusInWorkqueueToComplete(String id) {
        repository.setOrderStatusComplete(id);
    }

    public List<GpuWorkingItem> getGPUStatus() {
        return repository.getGPUStatus();
    }
    
    public void setGPUStatus(Integer gpuNum, String status) {
        repository.setGPUStatus(gpuNum, status);
    }

    public void beforeInferenceUpdate(String dbId) {
        repository.updateStatus(dbId, StatusConst.BEFORE_INFERENCE);        
    }

    public void afterInferenceUpdate(String dbId, String toonifyUrl, String headUrl) {
        repository.updateToonify(dbId, toonifyUrl);
        repository.updateHead(dbId, headUrl);
        repository.updateStatus(dbId, StatusConst.AFTER_INFERENCE);
    }

    public void initiation(String dbId) {
        repository.updateStatus(dbId, StatusConst.CREATED);
    }

    public InferenceResult makeHead(String dbId, String photoUrl, String style) {
        beforeInferenceUpdate(dbId);
        InferenceResult inferenceResult = inferenceServerInterface.inferenceHead(photoUrl, style);
        afterInferenceUpdate(dbId, inferenceResult.getToonifyUrl(), inferenceResult.getHeadUrl());
        return inferenceResult;
    }


}
