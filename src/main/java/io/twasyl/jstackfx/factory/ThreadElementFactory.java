package io.twasyl.jstackfx.factory;

import io.twasyl.jstackfx.beans.ThreadElement;
import io.twasyl.jstackfx.beans.ThreadReference;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible for creating {@link ThreadElement thread elements} properly.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.0
 */
public class ThreadElementFactory {
    protected static final Pattern LINE_STARTING_WITH_DASH = Pattern.compile("^\\s+-.+$");
    protected static final Pattern THREAD_NAME_PATTERN = Pattern.compile("^\\\"([^\\\"]+)\\\".+");
    protected static final Pattern THREAD_STATE_PATTERN = Pattern.compile("^\\s+java\\.lang\\.Thread\\.State: ([^ ]+).*$");
    protected static final Pattern THREAD_NUMBER_PATTERN = Pattern.compile("^[^#]+#([0-9]+).+");
    protected static final Pattern THREAD_PRIORITY_PATTERN = Pattern.compile("(\\sprio=([0-9]+))");
    protected static final Pattern THREAD_OS_PRIORITY_PATTERN = Pattern.compile("(os_prio=([0-9]+))");
    protected static final Pattern THREAD_ID_PATTERN = Pattern.compile("(tid=(0x[0-9a-f]+))");
    protected static final Pattern CALLING_STACK_LINE_MATCHER = Pattern.compile("^\\sat ([a-zA-Z\\.\\$]+)\\([a-zA-Z0-9 \\.:\\$]+\\)");
    protected static final Pattern LOCKED_OWNALBLE_SYNCHRONIZERS_START = Pattern.compile("^\\s+Locked ownable synchronizers:");
    protected static final Pattern LOCKED_SYNCHRONIZER_PATTERN = Pattern.compile("^\\s+-\\s<(0x[0-9a-f]+)>\\s\\(a ([a-zA-Z\\.\\$]+)\\)$");
    protected static final Pattern HOLDING_LOCKS_PATTERN = Pattern.compile("^\\s+- locked <(0x[0-9a-f]+)>\\s\\(a ([a-zA-Z\\.\\$]+)\\)$");
    protected static final Pattern WAITING_TO_LOCK_PATTERN = Pattern.compile("^\\s+- waiting to lock <(0x[0-9a-f]+)>\\s\\(a ([a-zA-Z\\.\\$]+)\\)$");
    protected static final Pattern PARKING_TO_WAIT_FOR_PATTERN = Pattern.compile("^\\s+- parking to wait for\\s+<(0x[0-9a-f]+)>\\s\\(a ([a-zA-Z\\.\\$]+)\\)$");

    public static ThreadElement build(final List<String> lines) {
        final ThreadElement element = new ThreadElement();

        element.setSource(buildSource(lines));

        String line = lines.get(0);
        element.setName(extractNameFrom(line));
        element.setNumber(extractThreadNumberFrom(line));
        element.setPriority(extractPriorityFrom(line));
        element.setOsPriority(extractOsPriorityFrom(line));
        element.setThreadId(extractThreadIdFrom(line));

        if(lines.size() > 1) {
            line = lines.get(1);
            element.setState(extractStateFrom(line));
        }

        element.setCallingStack(extractCallingStack(lines));
        element.getLockedSynchronizers().addAll(extractLockedSynchronizersFrom(lines));
        element.getHoldingLocks().addAll(extractHoldingLocks(lines));
        element.getWaitingToLock().addAll(extractWaitingToLock(lines));
        element.getParkingReasons().addAll(extractParkingToWaitFor(lines));

        return element;
    }

    protected static String buildSource(final List<String> lines) {
        final StringJoiner source = new StringJoiner("\n");
        lines.forEach(source::add);
        return source.toString();
    }

    protected static String extractNameFrom(final String line) {
        final Matcher matcher = THREAD_NAME_PATTERN.matcher(line);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    protected static Thread.State extractStateFrom(final String line) {
        final Matcher matcher = THREAD_STATE_PATTERN.matcher(line);

        if (matcher.find()) {
            return Thread.State.valueOf(matcher.group(1));
        }

        return null;
    }

    protected static long extractThreadNumberFrom(final String line) {
        final Matcher matcher = THREAD_NUMBER_PATTERN.matcher(line);

        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }

        return -1;
    }

    protected static String extractCallingStack(final List<String> lines) {
        final StringJoiner callingStack = new StringJoiner("\n");

        for(final String line : lines) {
            final Matcher lineMatcher = CALLING_STACK_LINE_MATCHER.matcher(line);

            if(lineMatcher.matches()) {
                callingStack.add(line.trim());
            }
        }

        return callingStack.toString();
    }

    protected static int extractPriorityFrom(final String line) {
        final Matcher matcher = THREAD_PRIORITY_PATTERN.matcher(line);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(2));
        }

        return -1;
    }

    protected static int extractOsPriorityFrom(final String line) {
        final Matcher matcher = THREAD_OS_PRIORITY_PATTERN.matcher(line);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(2));
        }

        return -1;
    }

    protected static String extractThreadIdFrom(final String line) {
        final Matcher matcher = THREAD_ID_PATTERN.matcher(line);

        if (matcher.find()) {
            return matcher.group(2);
        }

        return null;
    }

    protected static Set<ThreadReference> extractLockedSynchronizersFrom(final List<String> lines) {
        final Set<ThreadReference> synchronizers = new HashSet<>();

        boolean foundLockedOwnableSynchronizersLine = false;
        int index = 0;

        while (!foundLockedOwnableSynchronizersLine && index < lines.size()) {
            final String line = lines.get(index);
            foundLockedOwnableSynchronizersLine = LOCKED_OWNALBLE_SYNCHRONIZERS_START.matcher(line).matches();
            index++;
        }

        if (foundLockedOwnableSynchronizersLine) {
            boolean lineIsStartingWithDash = true;

            while (lineIsStartingWithDash && index < lines.size()) {
                final String line = lines.get(index);
                lineIsStartingWithDash = LINE_STARTING_WITH_DASH.matcher(line).matches();

                if (lineIsStartingWithDash) {
                    final Matcher synchronizerMatcher = LOCKED_SYNCHRONIZER_PATTERN.matcher(line);

                    if (synchronizerMatcher.matches()) {
                        final ThreadReference synchronizer = new ThreadReference();
                        synchronizer.setThreadId(synchronizerMatcher.group(1));
                        synchronizer.setClassName(synchronizerMatcher.group(2));
                        synchronizers.add(synchronizer);
                    }
                }

                index++;
            }
        }

        return synchronizers;
    }

    protected static Set<ThreadReference> extractWaitingToLock(final List<String> lines) {
        final Set<ThreadReference> waitingToLock = new LinkedHashSet<>();

        for (final String line : lines) {
            final Matcher waitingToLockMatcher = WAITING_TO_LOCK_PATTERN.matcher(line);

            if(waitingToLockMatcher.matches()) {
                final ThreadReference synchronizer = new ThreadReference();
                synchronizer.setThreadId(waitingToLockMatcher.group(1));
                synchronizer.setClassName(waitingToLockMatcher.group(2));
                waitingToLock.add(synchronizer);
            }
        }

        return waitingToLock;
    }

    protected static Set<ThreadReference> extractHoldingLocks(final List<String> lines) {
        final Set<ThreadReference> holdingLocks = new LinkedHashSet<>();

        for (final String line : lines) {
            final Matcher holdingLocksMatcher = HOLDING_LOCKS_PATTERN.matcher(line);

            if(holdingLocksMatcher.matches()) {
                final ThreadReference synchronizer = new ThreadReference();
                synchronizer.setThreadId(holdingLocksMatcher.group(1));
                synchronizer.setClassName(holdingLocksMatcher.group(2));
                holdingLocks.add(synchronizer);
            }
        }

        return holdingLocks;
    }

    protected static Set<ThreadReference> extractParkingToWaitFor(final List<String> lines) {
        final Set<ThreadReference> parkingReasons = new LinkedHashSet<>();

        for (final String line : lines) {
            final Matcher parkingMatcher = PARKING_TO_WAIT_FOR_PATTERN.matcher(line);

            if(parkingMatcher.matches()) {
                final ThreadReference synchronizer = new ThreadReference();
                synchronizer.setThreadId(parkingMatcher.group(1));
                synchronizer.setClassName(parkingMatcher.group(2));
                parkingReasons.add(synchronizer);
            }
        }

        return parkingReasons;
    }
}
