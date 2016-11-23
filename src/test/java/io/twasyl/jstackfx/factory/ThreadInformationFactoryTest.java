package io.twasyl.jstackfx.factory;

import io.twasyl.jstackfx.beans.LockedSynchronizer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Thierry Wasylczenko
 * @since jStackFX @@NEXT-VERSION@@
 */
public class ThreadInformationFactoryTest {

    @Test
    public void extractThreadName() {
        final String line = "\"Attach Listener\" #1060 daemon prio=9 os_prio=0 tid=0x0000000002be4800 nid=0x639b waiting on condition [0x0000000000000000]";
        final String expectedName = "Attach Listener";

        assertEquals(expectedName, ThreadInformationFactory.extractNameFrom(line));
    }

    @Test
    public void extractThreadNameInvalidPosition() {
        final String line = "Name: \"Attach Listener\" #1060 daemon prio=9 os_prio=0 tid=0x0000000002be4800 nid=0x639b waiting on condition [0x0000000000000000]";

        assertNull(ThreadInformationFactory.extractNameFrom(line));
    }

    @Test
    public void extractThreadState() {
        final String line = "   java.lang.Thread.State: RUNNABLE";
        final Thread.State expected = Thread.State.RUNNABLE;

        assertEquals(expected, ThreadInformationFactory.extractStateFrom(line));
    }

    @Test
    public void extractThreadStateWithLineContinuing() {
        final String line = "   java.lang.Thread.State: TIMED_WAITING (parking)";
        final Thread.State expected = Thread.State.TIMED_WAITING;

        assertEquals(expected, ThreadInformationFactory.extractStateFrom(line));
    }

    @Test
    public void extractThreadNumber() {
        final String line = "\"Attach Listener\" #1060 daemon prio=9 os_prio=0 tid=0x0000000002be4800 nid=0x639b waiting on condition [0x0000000000000000]";
        final long expected = 1060l;

        assertEquals(expected, ThreadInformationFactory.extractThreadNumberFrom(line));
    }

    @Test
    public void extractCallingStackWithStartIndicator() {
        final List<String> lines = new ArrayList<>();
        lines.add("\"process reaper\" #1054 daemon prio=10 os_prio=0 tid=0x00007f3f151a8800 nid=0x637d waiting on condition [0x00007f3f01b3a000]");
        lines.add("   java.lang.Thread.State: TIMED_WAITING (parking)");
        lines.add("\tat sun.misc.Unsafe.park(Native Method)");
        lines.add("\t- parking to wait for  <0x00000000d376b1e0> (a java.util.concurrent.SynchronousQueue$TransferStack)");
        lines.add("\tat java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)");
        lines.add("\tat java.util.concurrent.SynchronousQueue$TransferStack.awaitFulfill(SynchronousQueue.java:460)");
        lines.add("\tat java.util.concurrent.SynchronousQueue$TransferStack.transfer(SynchronousQueue.java:362)");
        lines.add("\tat java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:941)");
        lines.add("\tat java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1066)");
        lines.add("\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1127)");
        lines.add("\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)");
        lines.add("\tat java.lang.Thread.run(Thread.java:745)");
        lines.add("");
        lines.add("   Locked ownable synchronizers:");
        lines.add("\t- None");

        final String expected = "at sun.misc.Unsafe.park(Native Method)\n" +
                "at java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)\n" +
                "at java.util.concurrent.SynchronousQueue$TransferStack.awaitFulfill(SynchronousQueue.java:460)\n" +
                "at java.util.concurrent.SynchronousQueue$TransferStack.transfer(SynchronousQueue.java:362)\n" +
                "at java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:941)\n" +
                "at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1066)\n" +
                "at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1127)\n" +
                "at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)\n" +
                "at java.lang.Thread.run(Thread.java:745)";

        assertEquals(expected, ThreadInformationFactory.extractCallingStack(lines));
    }

    @Test
    public void extractPriority() {
        final String line = "\"process reaper\" #1054 daemon prio=10 os_prio=0 tid=0x00007f3f151a8800 nid=0x637d waiting on condition [0x00007f3f01b3a000]";
        final int expected = 10;

        assertEquals(expected, ThreadInformationFactory.extractPriorityFrom(line));
    }

    @Test
    public void extractOsPriority() {
        final String line = "\"process reaper\" #1054 daemon prio=10 os_prio=20 tid=0x00007f3f151a8800 nid=0x637d waiting on condition [0x00007f3f01b3a000]";
        final String expected = "0x00007f3f151a8800";

        assertEquals(expected, ThreadInformationFactory.extractThreadIdFrom(line));
    }

    @Test
    public void extractLockedSyncrhonizersWhenNone() {
        final List<String> lines = new ArrayList<>();
        lines.add("\"process reaper\" #1054 daemon prio=10 os_prio=0 tid=0x00007f3f151a8800 nid=0x637d waiting on condition [0x00007f3f01b3a000]");
        lines.add("   java.lang.Thread.State: TIMED_WAITING (parking)");
        lines.add("\tat sun.misc.Unsafe.park(Native Method)");
        lines.add("\t- parking to wait for  <0x00000000d376b1e0> (a java.util.concurrent.SynchronousQueue$TransferStack)");
        lines.add("\tat java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)");
        lines.add("\tat java.util.concurrent.SynchronousQueue$TransferStack.awaitFulfill(SynchronousQueue.java:460)");
        lines.add("\tat java.util.concurrent.SynchronousQueue$TransferStack.transfer(SynchronousQueue.java:362)");
        lines.add("\tat java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:941)");
        lines.add("\tat java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1066)");
        lines.add("\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1127)");
        lines.add("\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)");
        lines.add("\tat java.lang.Thread.run(Thread.java:745)");
        lines.add("");
        lines.add("   Locked ownable synchronizers:");
        lines.add("\t- None");

        final Set<LockedSynchronizer> synchronizers = ThreadInformationFactory.extractLockedSynchronizersFrom(lines);
        assertTrue(synchronizers.isEmpty());
    }

    @Test
    public void extractLockedSyncrhonizersWhenNotPresent() {
        final List<String> lines = new ArrayList<>();
        lines.add("\"process reaper\" #1054 daemon prio=10 os_prio=0 tid=0x00007f3f151a8800 nid=0x637d waiting on condition [0x00007f3f01b3a000]");
        lines.add("   java.lang.Thread.State: TIMED_WAITING (parking)");
        lines.add("\tat sun.misc.Unsafe.park(Native Method)");
        lines.add("\t- parking to wait for  <0x00000000d376b1e0> (a java.util.concurrent.SynchronousQueue$TransferStack)");
        lines.add("\tat java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)");
        lines.add("\tat java.util.concurrent.SynchronousQueue$TransferStack.awaitFulfill(SynchronousQueue.java:460)");
        lines.add("\tat java.util.concurrent.SynchronousQueue$TransferStack.transfer(SynchronousQueue.java:362)");
        lines.add("\tat java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:941)");
        lines.add("\tat java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1066)");
        lines.add("\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1127)");
        lines.add("\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)");
        lines.add("\tat java.lang.Thread.run(Thread.java:745)");
        lines.add("");

        final Set<LockedSynchronizer> synchronizers = ThreadInformationFactory.extractLockedSynchronizersFrom(lines);
        assertTrue(synchronizers.isEmpty());
    }

    @Test
    public void extractLockedSyncrhonizers() {
        final List<String> lines = new ArrayList<>();
        lines.add("\"process reaper\" #1054 daemon prio=10 os_prio=0 tid=0x00007f3f151a8800 nid=0x637d waiting on condition [0x00007f3f01b3a000]");
        lines.add("   java.lang.Thread.State: TIMED_WAITING (parking)");
        lines.add("\tat sun.misc.Unsafe.park(Native Method)");
        lines.add("\t- parking to wait for  <0x00000000d376b1e0> (a java.util.concurrent.SynchronousQueue$TransferStack)");
        lines.add("\tat java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:215)");
        lines.add("\tat java.util.concurrent.SynchronousQueue$TransferStack.awaitFulfill(SynchronousQueue.java:460)");
        lines.add("\tat java.util.concurrent.SynchronousQueue$TransferStack.transfer(SynchronousQueue.java:362)");
        lines.add("\tat java.util.concurrent.SynchronousQueue.poll(SynchronousQueue.java:941)");
        lines.add("\tat java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1066)");
        lines.add("\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1127)");
        lines.add("\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)");
        lines.add("\tat java.lang.Thread.run(Thread.java:745)");
        lines.add("");
        lines.add("   Locked ownable synchronizers:");
        lines.add("\t- <0x00000000cfa47920> (a java.util.concurrent.ThreadPoolExecutor$Worker)");
        lines.add("\t- <0x00000000cfa47921> (a java.util.concurrent.ThreadPoolExecutor$Worker)");
        lines.add("");

        final Set<LockedSynchronizer> synchronizers = ThreadInformationFactory.extractLockedSynchronizersFrom(lines);
        assertEquals(2, synchronizers.size());

        final Iterator<LockedSynchronizer> iterator = synchronizers.iterator();

        LockedSynchronizer synchronizer = iterator.next();
        assertEquals("0x00000000cfa47920", synchronizer.getThreadId());
        assertEquals("java.util.concurrent.ThreadPoolExecutor$Worker", synchronizer.getClassName());

        synchronizer = iterator.next();
        assertEquals("0x00000000cfa47921", synchronizer.getThreadId());
        assertEquals("java.util.concurrent.ThreadPoolExecutor$Worker", synchronizer.getClassName());
    }
}
