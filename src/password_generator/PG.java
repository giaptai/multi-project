package password_generator;

import java.security.SecureRandom;
import java.util.Random;
import java.util.Scanner;

import common.Constants;

public class PG {
    private String PasswordGenerator() {
        Scanner sc = new Scanner(System.in);
        StringBuilder sb = new StringBuilder();
        int charPos;
        int length;
        Random rand = new SecureRandom();
        System.out.print("Nhập độ dài mật khẩu: ");
        try {
            length = sc.nextInt();
        } catch (Exception e) {
            length = 13;
        } finally {
            sc.close();
        }
        for (int i = 0; i < length; i++) {
            int caseNum = rand.nextInt(4);
            switch (caseNum) {
                case 0:
                    charPos = rand.nextInt(Constants.DIGITS.length());
                    sb.append(Constants.DIGITS.charAt(charPos));
                    break;
                case 1:
                    charPos = rand.nextInt(Constants.LOWERCASE.length());
                    sb.append(Constants.LOWERCASE.charAt(charPos));
                    break;
                case 2:
                    charPos = rand.nextInt(Constants.UPPERCASE.length());
                    sb.append(Constants.UPPERCASE.charAt(charPos));
                    break;
                case 3:
                    charPos = rand.nextInt(Constants.SPECIAL_CHARACTERS.length());
                    sb.append(Constants.SPECIAL_CHARACTERS.charAt(charPos));
                    break;
                default:
                    break;
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(new PG().PasswordGenerator());
    }

}
