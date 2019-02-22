package work.yanghao;

import java.util.concurrent.*;

/**
 * This is a controller center and it control all thread to run with order
 */
public class ControlCenter {

    private static final ConcurrentLinkedQueue<Callable> tasks = new ConcurrentLinkedQueue<>();
    private static final ExecutorService executorService = Executors.newCachedThreadPool();


    public void startAllTasks(){

        //use ExecutorCompletionService ayn-not-block get other thread result
        ExecutorCompletionService executorCompletionService = new ExecutorCompletionService(executorService);

        //add the deamon
        createDeamon();

        //judged the tasks is exist or not
//        if(FIX_TASK_NUMBER!=tasks.size())
//        {
//            logger.error("The queue without any task or the size is not equal three");
//            return;
//        }

        //the thread pool hand out a thread for the tasks each other
        for (Callable task:tasks
        ) {
            executorCompletionService.submit(task);
        }

        //main thread,
        //this add extra task
        //when use the future.get() method will block the main thread until all the task is finished!
        //block the user thread and wait for all the tasks is completed
        /*while(true){
            System.out.println("thread main(0) wait all the thread executed ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~!!!!!!!!!!!!!!!");
            if(future1.isDone()&&future2.isDone()&&future3.isDone()){
                logger.debug("All the task was executed");
                break;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        //take method is block，poll method is not block
        try {
            for (int i = 0; i < 3; i++) {
//                String o = (String)executorCompletionService.take().get();
                Future task = executorCompletionService.take();
                Object o = task.get();
                System.out.println("get a result: " + o);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        //shutdown the thread pool
        executorService.shutdown();
        //terminate the user thread and destroy the deamon
        System.exit(0);
    }
    /**
     * the deamon is to watch the threads that running in background, and it will print out the info about the status.
     * use a dynamic strategy
     */
    private void createDeamon(){

        if(tasks.size()==0)
        {
            System.out.println("The queue without any task,the deamon is no need");
//            logger.error("The queue without any task,the deamon is no need");
            return;
        }
        //create a daemon to watch the task running in background
        Thread deamon = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("The active thread number created by thread pool is: "+((ThreadPoolExecutor) executorService).getActiveCount());
                    System.out.println("The block thread size is: "+((ThreadPoolExecutor) executorService).getQueue().size());
                    System.out.println("The completed thread number created by thread pool is: " + ((ThreadPoolExecutor) executorService).getCompletedTaskCount());
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    //                    logger.debug("The queue size is:" + tasks.size() + " The active thread number:"+((ThreadPoolExecutor)executorService).getActiveCount());
                    try {
                        TimeUnit.MILLISECONDS.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        deamon.setDaemon(true);
        deamon.start();
    }

    public void addTask2Queue(Callable task) {
        tasks.add(task);
    }
    public int getTaskNumber() {
        return tasks.size();
    }
}
