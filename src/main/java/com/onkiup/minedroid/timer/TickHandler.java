package com.onkiup.minedroid.timer;

import com.onkiup.minedroid.Context;
import com.onkiup.minedroid.Contexted;
import com.onkiup.minedroid.gui.Notification;
import com.onkiup.minedroid.gui.Overlay;
import com.onkiup.minedroid.gui.betterfonts.RepeatedTask;
import com.onkiup.minedroid.gui.primitives.Point;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * World ticks handler that runs timer
 */
public class TickHandler extends Contexted {
    private List<TaskInfo> tasks = new ArrayList<TaskInfo>();
    private List<TaskInfo> remove = new ArrayList<TaskInfo>();

    public TickHandler(Context context) {
        super(context);
        FMLCommonHandler.instance().bus().register(this);
    }


    /**
     * Handles client world tick
     *
     * @param event Tick information
     */
    @SubscribeEvent
    public synchronized void onClientTick(TickEvent.ClientTickEvent event) {
        runTasks(event, true);
    }

    /**
     * Handles server world tick
     * @param event
     */
    @SubscribeEvent
    public synchronized void onServerTick(TickEvent.ServerTickEvent event) {
        runTasks(event, false);
    }

    public synchronized void runTasks(TickEvent event, boolean client) {
        if (event.phase == TickEvent.Phase.END) {
            tasks.removeAll(remove);
            remove.clear();

            ArrayList<TaskInfo> tasksCopy = new ArrayList(tasks);

            for (TaskInfo task : tasksCopy) {
                if (task.task instanceof Task.Client && !client) continue;
                if (task.task instanceof Task.Server && client) continue;

                if (--task.left <= 0) {
                    task.task.execute(this);
                    if (task.repeatsLeft != 1) {
                        if (task.repeatsLeft > 0) {
                            task.repeatsLeft--;
                        }

                        task.left = task.interval;
                    } else {
                        remove.add(task);
                        if (task instanceof RepeatedTask) {
                            ((RepeatedTask) task).onDone(false);
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds a delayed task to tasks pool
     *
     * @param info task info
     */
    public void add(TaskInfo info) {
        tasks.add(info);
    }

    /**
     * Schedules task for delayed one-time execution
     *
     * @param time Delay interval in seconds
     * @param task Task to schedule
     * @return Task
     */
    public Task delay(float time, Task task) {
        add(new TaskInfo(task, (int) (time * 20), 1));
        return task;
    }

    /**
     * Schedules task for unlimited repeated execution
     *
     * @param time Task repeat interval
     * @param task Repeated task
     * @return DelayedTask
     */
    public Task repeat(float time, Task task) {
        add(new TaskInfo(task, (int) (time * 20), 0));
        return task;
    }

    /**
     * Schedules task for limited times repeated execution
     *
     * @param time  Task repeat interval
     * @param times Amount of repeats
     * @param task  Repeated task
     * @return DelayedTask
     */
    public Task repeat(float time, int times, Task task) {
        add(new TaskInfo(task, (int) (time * 20), times));
        return task;
    }

    /**
     * Prevents future executions of delayed task and removes it
     *
     * @param task Task to remove
     */
    public void stop(Task task) {
        delete(task);
    }


    /**
     * removes a delayed task from tasks pool
     *
     * @param info task info
     */
    public synchronized void delete(TaskInfo info) {
        remove.add(info);
    }

    /**
     * deletes a delayed task from tasks pool
     *
     * @param task delayed task
     */
    public synchronized void delete(Task task) {
        TaskInfo stop = null;
        for (TaskInfo info : tasks) {
            if (info.task == task) {
                stop = info;
                break;
            }
        }

        if (stop != null) {
            if (stop instanceof RepeatedTask && stop.repeatsLeft <= 0) {
                ((RepeatedTask) stop).onDone(true);
            }
            delete(stop);
        }
    }

    /**
     * Class for information about timer tasks
     */
    protected static class TaskInfo {
        Task task;
        int interval;
        int left;
        int repeatsLeft;

        public TaskInfo(Task task, int interval, int repeatsLeft) {
            this.task = task;
            this.interval = interval;
            this.left = interval;
            this.repeatsLeft = repeatsLeft;
        }
    }

}
