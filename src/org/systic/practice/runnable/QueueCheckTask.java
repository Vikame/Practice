package org.systic.practice.runnable;

import org.bukkit.scheduler.BukkitRunnable;
import org.systic.practice.Practice;
import org.systic.practice.matching.QueueManager;

public class QueueCheckTask extends BukkitRunnable {

    private final QueueManager queue_manager;

    public QueueCheckTask(){
        queue_manager = Practice.inst().queue_manager;
        runTaskTimerAsynchronously(Practice.inst(), 100, 100);
    }

    @Override
    public void run() {
        queue_manager.attemptMatchmaking();
    }

}
