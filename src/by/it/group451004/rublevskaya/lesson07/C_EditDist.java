package by.it.group451004.rublevskaya.lesson07;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class C_EditDist {

    String getDistanceEdinting(String one, String two) {
        int n = one.length();
        int m = two.length();

        // Создаем таблицу для хранения результатов подзадач
        int[][] dp = new int[n + 1][m + 1];

        // Базовые случаи
        for (int i = 0; i <= n; i++) {
            dp[i][0] = i; // Удаление всех символов из one
        }
        for (int j = 0; j <= m; j++) {
            dp[0][j] = j; // Вставка всех символов в one
        }

        // Заполнение таблицы
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (one.charAt(i - 1) == two.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // Символы совпадают
                } else {
                    dp[i][j] = 1 + Math.min(
                            Math.min(dp[i - 1][j], dp[i][j - 1]), // Удаление или вставка
                            dp[i - 1][j - 1] // Замена
                    );
                }
            }
        }

        // Восстановление операций
        StringBuilder result = new StringBuilder();
        int i = n, j = m;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && one.charAt(i - 1) == two.charAt(j - 1)) {
                // Копирование
                result.insert(0, "#,");
                i--;
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + 1) {
                // Удаление
                result.insert(0, "-" + one.charAt(i - 1) + ",");
                i--;
            } else if (j > 0 && dp[i][j] == dp[i][j - 1] + 1) {
                // Вставка
                result.insert(0, "+" + two.charAt(j - 1) + ",");
                j--;
            } else if (i > 0 && j > 0 && dp[i][j] == dp[i - 1][j - 1] + 1) {
                // Замена
                result.insert(0, "~" + two.charAt(j - 1) + ",");
                i--;
                j--;
            }
        }

        return result.toString();
    }

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_EditDist.class.getResourceAsStream("dataABC.txt");
        C_EditDist instance = new C_EditDist();
        Scanner scanner = new Scanner(stream);

        // Тестовые примеры
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
    }
}