package com.pasc.lib.displayads.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

/**
 * Created by huanglihou519 on 2018/4/13.
 */

public class TaskMachine {
    private Executor executor;

    private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    public TaskMachine(Executor executor) {
        this.executor = executor;
    }

    public void perfectTask(){
        while (!tasks.isEmpty()){
            Runnable task = tasks.poll();
            executor.execute(task);
        }
    }

    public void addTask(Runnable task){
        tasks.offer(task);
    }

    public void removeTask(Runnable task){
        tasks.remove(task);
    }

    public void clearAllTask(){
        tasks.clear();
    }
}
