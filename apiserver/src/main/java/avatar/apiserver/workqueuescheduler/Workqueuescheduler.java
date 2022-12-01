package avatar.apiserver.workqueuescheduler;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import avatar.apiserver.domain.GpuWorkingItem;
import avatar.apiserver.inferenceserver.InferenceResult;
import avatar.apiserver.inferenceserver.WebClientInferenceServerInterface;
import avatar.apiserver.serivce.WorkQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class Workqueuescheduler {
    
    private final WebClientInferenceServerInterface webClientInferenceServerInterface;
    private final WorkQueueService workqueueService;

    @PostConstruct
    @Transactional
    private void scheduling() {
        workqueueService.init();
        Thread schedulingThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3000);
                    getWaitingGPU().ifPresent((item) -> {
                        workqueueService.getNextWorkqueueOrder().ifPresent((order) ->{
                            log.info("order : {}, gpu : {}", order, item);
                            // gpu 상태를 변경 waiting -> loading
                            workqueueService.setGPUStatus(item.getNum(), "loading");

                            // order 상태를 변경 waiting -> loading
                            workqueueService.setUserStatusInWorkqueueToLoading(order.getId());

                            // dbItem 상태를 변경
                            workqueueService.beforeInferenceUpdate(order.getId());

                            // gpu에 따라 WebClient 변경
                            webClientInferenceServerInterface.setClient(item.getNum());

                            // inference
                            Thread inferenceRequestThread = new Thread(() -> {
                                    try {
                                        InferenceResult inferenceHead = webClientInferenceServerInterface.inferenceHead(order.getPhotoUrl(), order.getStyleCode());
                                        log.info(inferenceHead.toString());
                                        // order 상태를 변경
                                        workqueueService.setUserStatusInWorkqueueToComplete(order.getId());
                                        // dbItem 상태를 변경
                                        workqueueService.afterInferenceUpdate(order.getId(), inferenceHead.getToonifyUrl(), inferenceHead.getHeadUrl());
                                        workqueueService.setGPUStatus(item.getNum(), "waiting");
                                    } catch (Exception e) {
                                        // 원복
                                        log.info("inference request have some problem");
                                        workqueueService.setUserStatusInWorkqueueToWaiting(order.getId());
                                        workqueueService.initiation(order.getId());
                                        workqueueService.setGPUStatus(item.getNum(), "waiting");
                                    } 
                            });
                            inferenceRequestThread.start();
                        });
                    });
                } catch (Exception e) {
                    log.info("workqueue in error", e);
                }
            }
        }); 
        schedulingThread.start();
    }

    private Optional<GpuWorkingItem> getWaitingGPU() {
        List<GpuWorkingItem> gpuStatus = workqueueService.getGPUStatus();
        for (GpuWorkingItem item : gpuStatus) {
            if (item.getStatus().equals("waiting")) return Optional.of(item);
        }
        return Optional.empty();
    }
}
