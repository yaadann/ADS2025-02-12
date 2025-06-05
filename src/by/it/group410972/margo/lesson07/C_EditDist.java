package by.it.group410972.margo.lesson07;

import java.io.InputStream;
import java.util.Scanner;

public class C_EditDist {

    String getDistanceEdinting(String one, String two) {
        int m = one.length();
        int n = two.length();

        // Create a DP table
        int[][] dp = new int[m + 1][n + 1];

        // Initialize DP table
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i; // Deletions
        }

        for (int j = 0; j <= n; j++) {
            dp[0][j] = j; // Insertions
        }

        // Fill DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (one.charAt(i - 1) == two.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // No operation needed (match)
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, // Deletion
                                    dp[i][j - 1] + 1), // Insertion
                            dp[i - 1][j - 1] + 1); // Substitution
                }
            }
        }

        // Backtrack to find the operations
        StringBuilder result = new StringBuilder();
        int i = m, j = n;

        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && one.charAt(i - 1) == two.charAt(j - 1)) {
                result.insert(0, "#,"); // Match operation
                i--;
                j--;
            } else {
                int min = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]);

                if (min == dp[i - 1][j]) {
                    result.insert(0, "-" + one.charAt(i - 1) + ","); // Deletion
                    i--;
                } else if (min == dp[i][j - 1]) {
                    result.insert(0, "+" + two.charAt(j - 1) + ","); // Insertion
                    j--;
                } else {
                    result.insert(0, "~" + two.charAt(j - 1) + ","); // Substitution
                    i--;
                    j--;
                }
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        InputStream stream = C_EditDist.class.getResourceAsStream("dataABC.txt");
        C_EditDist instance = new C_EditDist();
        Scanner scanner = new Scanner(stream);

        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(), scanner.nextLine()));
    }
}