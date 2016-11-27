package io.twasyl.jstackfx.factory;

import io.twasyl.jstackfx.beans.Dump;
import io.twasyl.jstackfx.beans.ThreadInformation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Thierry Wasylczenko
 * @since jStackFX @@NEXT-VERSION@@
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
     * @param file
     * @return
     */
    public static Dump read(final File file) throws IOException {
        if (file == null) throw new NullPointerException("The file can not be null");
        if (!file.exists()) throw new FileNotFoundException("The file doesn't exist");

        final List<String> lines = Files.lines(file.toPath()).collect(Collectors.toList());
        final Dump dump;

        if (!lines.isEmpty()) {
            dump = new Dump();

            dump.setGenerationDateTime(LocalDateTime.parse(lines.get(0), DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")));
            dump.setDescription(lines.get(1));

            int beginningOfThreadIndex = 3;
            int endOfThreadIndex = 0;
            boolean beginningOfThreadFound = false;
            boolean endOfThreadFound = false;

            boolean vmThreadFound = false;
            boolean vmPeriodicTaskThreadFound = false;
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
                } else if(JNI_REFERENCES_PATTERN.matcher(line).matches()) {
                    endOfThreadFound = true;
                    jniReferencesFound = true;
                    endOfThreadIndex = index - 1;
                }

                if (beginningOfThreadFound && endOfThreadFound) {
                    if (isSingleLine(beginningOfThreadIndex, endOfThreadIndex)) {
                        if(jniReferencesFound) {
                            final Matcher result = JNI_REFERENCES_PATTERN.matcher(line);
                            if(result.matches()) {
                                dump.setNumberOfJNIRefs(Integer.parseInt(result.group(1)));
                            }
                        }
                    } else {
                        final ThreadInformation thread = ThreadInformationFactory.build(lines.subList(beginningOfThreadIndex, endOfThreadIndex));
                        thread.setDump(dump);
                        dump.getElements().add(thread);
                    }

                    beginningOfThreadFound = true;
                    endOfThreadFound = false;
                    beginningOfThreadIndex = index;
                } else {

//                    if (!vmThreadFound) {
//                        vmThreadFound = VM_THREAD_PATTERN.matcher(line).matches();
//                        firstEmptyLineFound = false;
//                        secondEmptyLinesFound = false;
//                    } else if ()
//
//
//                        boolean statisticLineFound =
//
//                    if (!statisticLineFound) {
//                        GC_TASK_THREAD_PATTERN
//                    }
                }
            }
        } else {
            dump = null;
        }

        return dump;
    }

    protected static boolean isSingleLine(int firstLine, int secondLine) {
        return secondLine - firstLine == 1;
    }
}
