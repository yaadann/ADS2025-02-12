package by.it.group410901.borisdubinin.lesson15;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.charset.MalformedInputException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class SourceScannerB {
    public static void main(String[] args) {
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;

        try {
            List<FileData> processedFiles = processJavaFiles(src);
            printResults(processedFiles);
        } catch (IOException e) {
            System.err.println("Ошибка при обработке файлов: " + e.getMessage());
        }
    }

    private static List<FileData> processJavaFiles(String srcDir) throws IOException {
        List<FileData> result = new ArrayList<>();
        Path srcPath = Paths.get(srcDir);

        if (!Files.exists(srcPath)) {
            System.err.println("Каталог src не найден: " + srcDir);
            return result;
        }

        Files.walkFileTree(srcPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".java")) {
                    processFile(file, srcPath, result);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                if (exc instanceof MalformedInputException) {
                    System.err.println("Пропущен файл с ошибкой кодировки: " + file);
                    return FileVisitResult.CONTINUE;
                }
                return super.visitFileFailed(file, exc);
            }
        });

        return result;
    }

    private static void processFile(Path file, Path srcPath, List<FileData> result) {
        try {
            String content = readFileIgnoringEncodingErrors(file);

            // Пропускаем тестовые файлы
            if (content.contains("@Test") || content.contains("org.junit.Test")) {
                return;
            }

            String processedContent = processContent(content);
            byte[] bytes = processedContent.getBytes(StandardCharsets.UTF_8);
            String relativePath = srcPath.relativize(file).toString();

            result.add(new FileData(relativePath, bytes.length, bytes));

        } catch (MalformedInputException e) {
            System.err.println("Ошибка кодировки в файле: " + file);
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + file + " - " + e.getMessage());
        }
    }

    private static String readFileIgnoringEncodingErrors(Path file) throws IOException {
        try {
            return Files.readString(file, StandardCharsets.UTF_8);
        } catch (MalformedInputException e) {
            // Пробуем прочитать файл с другой кодировкой или игнорируем ошибки
            byte[] bytes = Files.readAllBytes(file);
            return new String(bytes, StandardCharsets.ISO_8859_1);
        }
    }

    private static String processContent(String content) {
        // Удаляем package и imports, комментарии и пустые строки за один проход O(n)
        StringBuilder result = new StringBuilder();
        boolean inBlockComment = false;
        boolean inLineComment = false;
        boolean inString = false;
        boolean inChar = false;
        char prevChar = 0;

        for (int i = 0; i < content.length(); i++) {
            char currentChar = content.charAt(i);

            // Обработка строковых литералов
            if (!inBlockComment && !inLineComment && currentChar == '"' && prevChar != '\\') {
                inString = !inString;
                result.append(currentChar);
            }
            // Обработка символьных литералов
            else if (!inBlockComment && !inLineComment && currentChar == '\'' && prevChar != '\\') {
                inChar = !inChar;
                result.append(currentChar);
            }
            // Если внутри строки или символа - просто добавляем символ
            else if (inString || inChar) {
                result.append(currentChar);
            }
            // Обработка блочных комментариев
            else if (!inLineComment && currentChar == '/' && i + 1 < content.length() && content.charAt(i + 1) == '*') {
                inBlockComment = true;
                i++; // Пропускаем следующий символ '*'
            }
            // Конец блочного комментария
            else if (inBlockComment && currentChar == '*' && i + 1 < content.length() && content.charAt(i + 1) == '/') {
                inBlockComment = false;
                i++; // Пропускаем следующий символ '/'
            }
            // Обработка строчных комментариев
            else if (!inBlockComment && currentChar == '/' && i + 1 < content.length() && content.charAt(i + 1) == '/') {
                inLineComment = true;
                i++; // Пропускаем следующий символ '/'
            }
            // Конец строчного комментария (конец строки)
            else if (inLineComment && (currentChar == '\n' || currentChar == '\r')) {
                inLineComment = false;
                result.append(currentChar);
            }
            // Если не в комментарии - добавляем символ
            else if (!inBlockComment && !inLineComment) {
                result.append(currentChar);
            }
            // В противном случае (в комментарии) - пропускаем символ

            prevChar = currentChar;
        }

        // Теперь удаляем package, imports и пустые строки
        String withoutComments = result.toString();
        return removePackageImportsAndEmptyLines(withoutComments);
    }

    private static String removePackageImportsAndEmptyLines(String content) {
        String[] lines = content.split("\n", -1);
        StringBuilder result = new StringBuilder();
        boolean previousLineWasEmpty = false;

        for (String line : lines) {
            String trimmedLine = line.trim();

            // Пропускаем package и import строки
            if (trimmedLine.startsWith("package ") || trimmedLine.startsWith("import ")) {
                continue;
            }

            // Пропускаем пустые строки
            if (trimmedLine.isEmpty()) {
                previousLineWasEmpty = true;
                continue;
            }

            // Добавляем строку (с одним переносом, если предыдущая была не пустой)
            if (result.length() > 0 && !previousLineWasEmpty) {
                result.append("\n");
            }
            result.append(line);
            previousLineWasEmpty = false;
        }

        // Удаляем символы с кодом <33 в начале и конце
        String finalContent = result.toString();
        finalContent = trimLowChars(finalContent);

        return finalContent;
    }

    private static String trimLowChars(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        // Обрезаем в начале
        int start = 0;
        while (start < str.length() && str.charAt(start) < 33) {
            start++;
        }

        // Обрезаем в конце
        int end = str.length();
        while (end > start && str.charAt(end - 1) < 33) {
            end--;
        }

        return str.substring(start, end);
    }

    private static void printResults(List<FileData> files) {
        // Сортируем по размеру (по возрастанию), затем по пути
        files.sort((f1, f2) -> {
            int sizeCompare = Integer.compare(f1.size, f2.size);
            if (sizeCompare != 0) {
                return sizeCompare;
            }
            return f1.path.compareTo(f2.path);
        });

        // Выводим результаты
        for (FileData file : files) {
            System.out.println(file.size + " " + file.path);
        }
    }

    private static class FileData {
        String path;
        int size;
        byte[] content;

        FileData(String path, int size, byte[] content) {
            this.path = path;
            this.size = size;
            this.content = content;
        }
    }
}
