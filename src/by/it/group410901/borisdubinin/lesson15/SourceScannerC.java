package by.it.group410901.borisdubinin.lesson15;

import java.io.*;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class SourceScannerC {

    private static final int COPY_THRESHOLD = 10;

    public static void main(String[] args) {
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;

        try {
            List<FileData> processed = new ArrayList<>(256);

            Files.walkFileTree(Paths.get(src), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (!file.toString().endsWith(".java"))
                        return FileVisitResult.CONTINUE;

                    try {
                        String text = readFileSafe(file);
                        if (text.contains("@Test") || text.contains("org.junit.Test"))
                            return FileVisitResult.CONTINUE;

                        String cleaned = preprocess(text);
                        processed.add(new FileData(file.toString(), cleaned));

                    } catch (IOException e) {
                        System.err.println("Ошибка чтения: " + file + " — " + e.getMessage());
                    }

                    return FileVisitResult.CONTINUE;
                }
            });

            processed.sort(Comparator.comparing(fd -> fd.path));

            detectCopies(processed);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Безопасное чтение файла
    private static String readFileSafe(Path path) throws IOException {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(path);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (MalformedInputException ex) {
            // fallback, чтобы не ломать работу
            bytes = Files.readAllBytes(path);
            return new String(bytes, StandardCharsets.ISO_8859_1);
        }
    }

    // Препроцессинг
    private static String preprocess(String text) {
        StringBuilder sb = new StringBuilder(text.length());

        // 1. Удаление package + import
        for (String line : text.split("\n")) {
            String t = line.trim();
            if (t.startsWith("package ") || t.startsWith("import "))
                continue;
            sb.append(line).append('\n');
        }

        // 2. Удаление комментариев за O(n)
        String noComments = stripComments(sb);

        // 3. Замена символов < 33 на ' ' + trim
        return normalizeSpaces(noComments).trim();
    }

    // Удаление комментариев
    private static String stripComments(CharSequence text) {
        StringBuilder out = new StringBuilder(text.length());
        int n = text.length();

        boolean inSL = false;  // single-line
        boolean inML = false;  // multi-line
        boolean inStr = false; // "string"
        boolean inChr = false; // 'c'
        boolean esc = false;

        for (int i = 0; i < n; i++) {
            char c = text.charAt(i);
            char next = (i + 1 < n) ? text.charAt(i + 1) : '\0';

            if (inSL) {
                if (c == '\n') {
                    inSL = false;
                    out.append(c);
                }
                continue;
            }

            if (inML) {
                if (c == '*' && next == '/') {
                    inML = false;
                    i++;
                }
                continue;
            }

            if (inStr) {
                out.append(c);
                if (c == '"' && !esc)
                    inStr = false;
                esc = c == '\\' && !esc;
                continue;
            }

            if (inChr) {
                out.append(c);
                if (c == '\'' && !esc)
                    inChr = false;
                esc = c == '\\' && !esc;
                continue;
            }

            // Начало комментария
            if (c == '/' && next == '/') {
                inSL = true;
                i++;
                continue;
            }
            if (c == '/' && next == '*') {
                inML = true;
                i++;
                continue;
            }

            // Строка
            if (c == '"') {
                inStr = true;
                out.append(c);
                continue;
            }

            // Символьный литерал
            if (c == '\'') {
                inChr = true;
                out.append(c);
                continue;
            }

            out.append(c);
        }
        return out.toString();
    }

    // Нормализация пробельных символов
    private static String normalizeSpaces(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            sb.append(c < 33 ? ' ' : c);
        }
        return sb.toString();
    }

    // Поиск копий
    private static void detectCopies(List<FileData> list) {
        int n = list.size();

        for (int i = 0; i < n; i++) {
            FileData a = list.get(i);
            List<String> copies = new ArrayList<>();

            for (int j = i + 1; j < n; j++) {
                FileData b = list.get(j);

                // 1) Быстрый предфильтр по длине
                if (Math.abs(a.text.length() - b.text.length()) >= COPY_THRESHOLD)
                    continue;

                // 2) Быстрый предфильтр по хешу
                if (a.hash != b.hash)
                    continue;

                // 3) Левенштейн с обрывом при > 10
                int dist = levenshteinLimited(a.text, b.text, COPY_THRESHOLD);
                if (dist < COPY_THRESHOLD)
                    copies.add(b.path);
            }

            if (!copies.isEmpty()) {
                System.out.println(a.path);
                for (String p : copies) {
                    System.out.print("  copy: ");
                    System.out.println(p);
                }
            }
        }
    }

    // Ограниченный Левенштейн
    private static int levenshteinLimited(String a, String b, int limit) {
        int n = a.length();
        int m = b.length();

        if (Math.abs(n - m) >= limit) return limit;

        int[] dpPrev = new int[m + 1];
        int[] dpCurr = new int[m + 1];

        for (int j = 0; j <= m; j++)
            dpPrev[j] = j;

        for (int i = 1; i <= n; i++) {
            dpCurr[0] = i;
            char ca = a.charAt(i - 1);
            int minRow = dpCurr[0];

            for (int j = 1; j <= m; j++) {
                int cost = (ca == b.charAt(j - 1)) ? 0 : 1;

                dpCurr[j] = Math.min(
                        Math.min(dpPrev[j] + 1, dpCurr[j - 1] + 1),
                        dpPrev[j - 1] + cost
                );

                if (dpCurr[j] < minRow) minRow = dpCurr[j];
            }

            if (minRow >= limit)
                return limit;

            int[] tmp = dpPrev;
            dpPrev = dpCurr;
            dpCurr = tmp;
        }

        return dpPrev[m];
    }

    //======================================================================

    private static class FileData {
        final String path;
        final String text;
        final int hash;

        FileData(String path, String text) {
            this.path = path;
            this.text = text;
            this.hash = murmurHash(text);
        }
    }

    // Быстрый хеш для предфильтра
    private static int murmurHash(String s) {
        int h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = h * 0x5bd1e995 ^ s.charAt(i);
        }
        return h;
    }
}

