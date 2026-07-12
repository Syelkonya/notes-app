package su.ternovskii.notificationservice.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class SendTimingAspect {

    @Around("execution(* su.ternovskii.notificationservice.sender.*.send(..)) " +
            "&& !target(su.ternovskii.notificationservice.sender.SmsSender)")
    public Object logSendTiming(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.nanoTime();
        try {
            return proceedingJoinPoint.proceed();
        } finally {
            long timeMs = (System.nanoTime() - start) / 1_000_000;
            log.info("method {} took {} ms",
                    proceedingJoinPoint.getSignature().toShortString(),
                    timeMs);
        }
    }
}
