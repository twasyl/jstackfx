package io.twasyl.jstackfx.factory;

import io.twasyl.jstackfx.beans.Dump;
import io.twasyl.jstackfx.beans.FileDump;
import io.twasyl.jstackfx.beans.InMemoryDump;
import io.twasyl.jstackfx.beans.ThreadElement;
import io.twasyl.jstackfx.exceptions.DumpException;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class is responsible for creating correctly {@link Dump dumps} instances. {@link Dump Dumps} can be instantiated
 * from a files or from a collections of lines representing the thread dump.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.0
 */
public class DumpFactory {

    protected static final String BEGINNING_OF_THREAD = "\"";
    protected static final Pattern VM_THREAD_PATTERN = Pattern.compile("^\\\"VM Thread\\\".+$");
    protected static final Pattern GC_TASK_THREAD_PATTERN = Pattern.compile("\\\"GC task thread#.+");
    protected static final Pattern VM_PERIODIC_TASK_THREAD_PATTERN = Pattern.compile("\\\"VM Periodic Task Thread\\\".+");
    protected static final Pattern JNI_REFERENCES_PATTERN = Pattern.compile("^JNI global references: ([0-9]*)$");

    /**
     * Reads a given jstack file and create the associated {@link Dump} object.
     *
     * @param file The thread dump file to read.
     * @return A dump object.
     */
    public static Dump read(final File file) throws IOException, InstantiationException, IllegalAccessException {
        if (file == null) throw new NullPointerException("The file can not be null");
        if (!file.exists()) throw new FileNotFoundException("The file doesn't exist");

        final List<String> lines = Files.lines(file.toPath()).collect(Collectors.toList());
        final FileDump dump = createDumpInstance(FileDump.class, lines);
        dump.setFile(file);

        return dump;
    }

    /**
     * Reads a given collections of lines coming from a thread dump and create the associated {@link Dump} object.
     *
     * @param lines The lines of the thread dump.
     * @return A dump object.
     */
    public static Dump read(final List<String> lines) throws IOException, InstantiationException, IllegalAccessException {
        final InMemoryDump dump = createDumpInstance(InMemoryDump.class, lines);
        dump.getLines().addAll(lines);

        return dump;
    }

    private static <D extends Dump> D createDumpInstance(Class<D> dumpClass, final List<String> lines) throws IllegalAccessException, InstantiationException {
        final D dump;

        if (!lines.isEmpty()) {
            dump = dumpClass.newInstance();

            dump.setGenerationDateTime(LocalDateTime.parse(lines.get(0), DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")));
            dump.setDescription(lines.get(1));

            int beginningOfThreadIndex = 3;
            int endOfThreadIndex = 0;
            boolean beginningOfThreadFound = false;
            boolean endOfThreadFound = false;
            boolean jniReferencesFound = false;

            for (int index = beginningOfThreadIndex; index < lines.size(); index++) {
                final String line = lines.get(index);

                if (line.startsWith(BEGINNING_OF_THREAD)) {
                    if (!beginningOfThreadFound) {
                        beginningOfThreadFound = true;
                        beginningOfThreadIndex = index;
                    } else if (!endOfThreadFound) {
                        endOfThreadFound = true;
                        endOfThreadIndex = index - 1;
                    }
                } else if (JNI_REFERENCES_PATTERN.matcher(line).matches()) {
                    endOfThreadFound = true;
                    jniReferencesFound = true;
                    endOfThreadIndex = index - 1;
                }

                if (beginningOfThreadFound && endOfThreadFound) {
                    if (isSingleLine(beginningOfThreadIndex, endOfThreadIndex)) {
                        if (jniReferencesFound) {
                            final Matcher result = JNI_REFERENCES_PATTERN.matcher(line);
                            if (result.matches()) {
                                dump.setNumberOfJNIRefs(Integer.parseInt(result.group(1)));
                            }
                        }
                    } else {
                        final ThreadElement thread = ThreadElementFactory.build(lines.subList(beginningOfThreadIndex, endOfThreadIndex));
                        thread.setDump(dump);
                        dump.getElements().add(thread);
                    }

                    beginningOfThreadFound = true;
                    endOfThreadFound = false;
                    beginningOfThreadIndex = index;
                }
            }
        } else {
            dump = null;
        }

        return dump;
    }

    /**
     * Take a thread dump for a given process. The {@code jstack} tool should be in the {@code PATH} in order to be
     * launched by the application.
     *
     * @param processId The ID of the process to make a thread dump for.
     * @return The thread dump.
     */
    public static Dump make(final long processId) throws DumpException {
        final Process jstack;
        try {
            jstack = new ProcessBuilder("jstack", String.valueOf(processId))
                    .redirectErrorStream(true)
                    .start();

            final List<String> lines = getLinesFrom(jstack);

            if (jstack.waitFor() == 0) {
                return read(lines);
            } else {
                final StringJoiner error = new StringJoiner("\n");
                lines.forEach(error::add);
                throw new DumpException(error.toString());
            }
        } catch (IOException | InterruptedException | IllegalAccessException | InstantiationException e) {
            throw new DumpException(e);
        }
    }

    /**
     * Reads a {@link Process} input and create a collection of lines from it.
     *
     * @param process The process to capture the input.
     * @return A collection of lines corresponding to the process' output.
     * @throws DumpException If something went wrong.
     */
    protected static List<String> getLinesFrom(final Process process) throws DumpException {
        final List<String> lines = new ArrayList<>();

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new DumpException(e);
        }

        return lines;
    }

    protected static boolean isSingleLine(int firstLine, int secondLine) {
        return secondLine - firstLine == 1;
    }
}
