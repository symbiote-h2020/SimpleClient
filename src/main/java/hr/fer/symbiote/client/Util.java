package hr.fer.symbiote.client;

import java.util.concurrent.Callable;

public class Util {
    public static <R> R wrapException(Callable<R> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException("Wrapped exception", e);
        }
    }

    public static void wrapException(NoReturnBlock block) {
        try {
            block.doWork();
        } catch (Exception e) {
            throw new RuntimeException("Wrapped exception", e);
        }
    }
    
    public interface NoReturnBlock {
        void doWork() throws Exception;
    }
}
