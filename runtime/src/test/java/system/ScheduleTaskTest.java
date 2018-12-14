package system;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Author: yyuze
 * Time: 2018-12-11
 */
public class ScheduleTaskTest {


    @Test
    public void test() {


    }


    public static void main(String[] args){
//        int corePoolSize = 4;
//        int maximumPoolSize = 6;
//
//        long keepAliveTime = 500;
//        TimeUnit timeUnit = TimeUnit.MICROSECONDS;
//
//        ThreadPoolExecutor pool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime,timeUnit,new ArrayBlockingQueue<>(maximumPoolSize));
//
//        ReentrantLock deamonLock = new ReentrantLock();
//        Condition condition = deamonLock.newCondition();
//        pool.execute(()->{
//            try {
//                deamonLock.deamonLock();
//                System.out.println("i am thread 1, i m going to sleep");
//                condition.await();
//                System.out.println("i am thread 1, i m awake");
//                if(pool.isTerminating()){
//                    System.out.println("thread poor is terminating");
//                }
//                pool.shutdown();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }finally {
//                deamonLock.unlock();
//            }
//        });
//
//        pool.submit(()->{
//            deamonLock.deamonLock();
//            try{
//                long start = System.currentTimeMillis();
//                int i = 1;
//                System.out.println("i am thread 2, i m going to count");
//                while(System.currentTimeMillis() - start <5001){
//                    System.out.println(i);
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    i++;
//                }
//                condition.signal();
//            }finally {
//                deamonLock.unlock();
//            }
//        });
//
//        while(!pool.isTerminated()){
//            if(pool.isShutdown()){
//                System.out.println("thread poor shut down");
//            }
//        }
//        System.out.println("thread poor terminated");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while(true){
                String line = reader.readLine();
                if (line.equals("")) {
                    break;
                }
                System.out.println(line);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}
