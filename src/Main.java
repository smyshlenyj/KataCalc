import java.util.Scanner;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class Main {
    static boolean arabic = false;
    static boolean roman = false;

    enum RomanNumeral {
        I(1), IV(4), V(5), IX(9), X(10);

        private int value;

        RomanNumeral(int value) {
            this.value = value;
        }

        int getValue() {
            return value;
        }

        static List<RomanNumeral> getReverseSortedValues() {
            return Arrays.stream(values())
                    .sorted(Comparator.comparing((RomanNumeral e) -> e.value).reversed())
                    .collect(Collectors.toList());
        }
    }

    static int romanToArabic(String input) throws RuntimeException {
        int countDigits = 0;
        for (int i = 1; i < input.length(); i++)
            if (input.charAt(i) == input.charAt(0))
                countDigits++;
        if (countDigits > 2) throw new RuntimeException("Invalid number detected");

        String romanNumeral = input.toUpperCase();
        int result = 0;

        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;
        while ((!romanNumeral.isEmpty()) && (i < romanNumerals.size())) {
            RomanNumeral symbol = romanNumerals.get(i);
            if (romanNumeral.startsWith(symbol.name())) {
                result += symbol.getValue();
                romanNumeral = romanNumeral.substring(symbol.name().length());
            } else {
                i++;
            }
        }

        if (!romanNumeral.isEmpty()) {
            return -1;
        }

        return result;
    }

    static int left(String input, int i) throws RuntimeException {
        String numberCandidate = input.substring(0, i).trim();
        int temp = -1;

        if (romanToArabic(numberCandidate) != -1) {
            temp = romanToArabic(numberCandidate);
            roman = true;
        }

        if (romanToArabic(numberCandidate) == -1) {
            temp = Integer.parseInt(numberCandidate);
            arabic = true;
        }

        if (temp > 0 && temp <= 10)
            return temp;
        else throw new RuntimeException("Number is out of allowed range");
    }

    static String arabicToRoman(int number) throws IllegalArgumentException {
        if ((number <= 0) || (number > 20)) {
            throw new IllegalArgumentException(number + " is not in range [1,20]");
        }

        List<RomanNumeral> romanNumerals = RomanNumeral.getReverseSortedValues();

        int i = 0;
        StringBuilder sb = new StringBuilder();

        while ((number > 0) && (i < romanNumerals.size())) {
            RomanNumeral currentSymbol = romanNumerals.get(i);
            if (currentSymbol.getValue() <= number) {
                sb.append(currentSymbol.name());
                number -= currentSymbol.getValue();
            } else {
                i++;
            }
        }
        return sb.toString();
    }

    static int right(String input, int i) throws RuntimeException {
        String numberCandidate = input.substring(i + 1).trim();
        int temp = -1;

        if (romanToArabic(numberCandidate) != -1 && !arabic) {
            temp = romanToArabic(numberCandidate);
        } else if (romanToArabic(numberCandidate) == -1 && !roman) {
            temp = Integer.parseInt(numberCandidate);
        } else if (temp <= 0 || temp > 10)
            throw new RuntimeException("Number is out of allowed range");

        return temp;
    }

    static boolean checkExpression(String input) {
        int countOperators = 0;
        Pattern pattern = Pattern.compile("[+/*-]");
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            countOperators++;
        }
        return countOperators == 1;
    }

    public static void main(String[] args) throws RuntimeException {
        while (true) {
            System.out.print("Straight to the point: enter an expression:\n");
            Scanner scanner = new Scanner(System.in);
            String inputString = scanner.nextLine();
            if (!checkExpression(inputString))
                throw new RuntimeException("Invalid expression: no suitable operator or too many operators");
            int result = 0;

            for (int i = 0; i < inputString.length(); i++) {

                if (inputString.charAt(i) == '+') {
                    result = left(inputString, i) + right(inputString, i);
                    break;
                }

                if (inputString.charAt(i) == '-') {
                    result = left(inputString, i) - right(inputString, i);
                    break;
                }

                if (inputString.charAt(i) == '/') {
                    result = left(inputString, i) / right(inputString, i);
                    break;
                }

                if (inputString.charAt(i) == '*') {
                    result = left(inputString, i) * right(inputString, i);
                    break;
                }
            }
            if (roman) System.out.println(arabicToRoman(result));
            else
                System.out.println(result);

            roman = false;
            arabic = false;
        }
    }
}