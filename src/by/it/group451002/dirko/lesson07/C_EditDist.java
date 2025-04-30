package by.it.group451002.dirko.lesson07;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
Задача на программирование: расстояние Левенштейна
    https://ru.wikipedia.org/wiki/Расстояние_Левенштейна
    http://planetcalc.ru/1721/

Дано:
    Две данных непустые строки длины не более 100, содержащие строчные буквы латинского алфавита.

Необходимо:
    Решить задачу МЕТОДАМИ ДИНАМИЧЕСКОГО ПРОГРАММИРОВАНИЯ
    Итерационно вычислить алгоритм преобразования двух данных непустых строк
    Вывести через запятую редакционное предписание в формате:
     операция("+" вставка, "-" удаление, "~" замена, "#" копирование)
     символ замены или вставки

    Sample Input 1:
    ab
    ab
    Sample Output 1:
    #,#,

    Sample Input 2:
    short
    ports
    Sample Output 2:
    -s,~p,#,#,#,+s,

    Sample Input 3:
    distance
    editing
    Sample Output 2:
    +e,#,#,-s,#,~i,#,-c,~g,


    P.S. В литературе обычно действия редакционных предписаний обозначаются так:
    - D (англ. delete) — удалить,
    + I (англ. insert) — вставить,
    ~ R (replace) — заменить,
    # M (match) — совпадение.
*/


public class C_EditDist {

    String getDistanceEdinting(String one, String two) {
        //!!!!!!!!!!!!!!!!!!!!!!!!!     НАЧАЛО ЗАДАЧИ     !!!!!!!!!!!!!!!!!!!!!!!!!

        // Возвращаемый результат - строка
        String result = "";

        // Инициализация двумерного массива (таблички)
        int[][] levenTable = new int[one.length() + 1][two.length() + 1];

        // Заполняем первый столбец соответствующими индексами
        for (int i = 0; i <= one.length(); i++) {
            levenTable[i][0] = i;
        }

        // Заполняем первую строку соответствующими индексами
        for (int j = 0; j <= two.length(); j++) {
            levenTable[0][j] = j;
        }

        // Итерационно заполняем табличку Левенштейна
        for (int i = 1; i <= one.length(); i++) {
            for (int j = 1; j <= two.length(); j++) {
                int c = (one.charAt(i - 1) == two.charAt(j - 1)) ? 0 : 1;
                levenTable[i][j] = Math.min(Math.min(levenTable[i - 1][j] + 1, levenTable[i][j - 1] + 1),
                        levenTable[i-1][j-1] + c);
            }
        }

        // Создаем List, в котором будем хранить редакционное предписание
        List<Character> res = new ArrayList<>();

        // Восстанавливаем решение проходя с последней ячейки в первую
        int i = one.length(), j = two.length();
        while (i > 0 || j > 0) {
            // Если D[i, j] = D[i - 1, j - 1] + Diff(A[i], B[i]
            if ((i > 0 && j > 0) && levenTable[i][j] == levenTable[i - 1][j - 1] +
                        (one.charAt(i - 1) == two.charAt(j - 1) ? 0 : 1)) {
                // Если равно, то выясняем, что это: замена или соответствие
                if ((one.charAt(i - 1) == two.charAt(j - 1) ? 0 : 1) == 0) {
                    res.addFirst('#');
                }
                else { res.addFirst('~'); }
                i--; j--;
            }
            // Иначе, если D[i, j] = D[i - 1][j] + 1, то это удаление
            else if (i > 0 && levenTable[i][j] == levenTable[i - 1][j] + 1) {
                res.addFirst('-');
                i--;
            }
            // Иначе (D[i, j] = D[i, j - 1] + 1), это вставка
            else {
                res.addFirst('+');
                j--;
            }
        }

        // Инициализация текущих индексов в первой и второй строках, а также в результате
        int lenOne = 0, lenTwo = 0; i = 0;
        while (i < res.size()) {
            // Если это вставка, то указываем вставляемый символ
            if (res.get(i) == '+') {
                res.add(i + 1, two.charAt(lenTwo));
                lenTwo++;
            }
            // Если удаление, то указываем удаляемый символ
            else if (res.get(i) == '-') {
                res.add(i + 1, one.charAt(lenOne));
                lenOne++;
            }
            // Если это замена, то указываем на какой символ заменяем
            else if (res.get(i) == '~') {
                res.add(i + 1, two.charAt(lenTwo));
                lenOne++; lenTwo++;
            }
            // Иначе, это соответствие и ничего указывать не надо
            else {
                lenOne++; lenTwo++;
                i--;
            }
            i += 3;
            res.add(i - 1, ',');
        }

        // Конвертируем List в строку
        for (Character c : res) { result += c; }

        //!!!!!!!!!!!!!!!!!!!!!!!!!     КОНЕЦ ЗАДАЧИ     !!!!!!!!!!!!!!!!!!!!!!!!!
        return result;
    }


    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_EditDist.class.getResourceAsStream("dataABC.txt");
        C_EditDist instance = new C_EditDist();
        Scanner scanner = new Scanner(stream);
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(),scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(),scanner.nextLine()));
        System.out.println(instance.getDistanceEdinting(scanner.nextLine(),scanner.nextLine()));
    }

}