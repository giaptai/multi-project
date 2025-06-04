package short_url;

import common.Constants;

/**
 * Ý tưởng cốt lõi là chuyển đổi ID tự tăng của bản ghi trong cơ sở dữ liệu
 * thành
 * một mã ngắn (short code) duy nhất bằng hệ cơ số 62.
 *
 * <br>
 * <br>
 *
 * ### 1. Quá trình rút gọn URL (Shortening Process)
 * Khi người dùng yêu cầu rút gọn một URL gốc (long URL):
 * <ol>
 * <li>
 * <strong>Lưu URL gốc vào DB:</strong> Ứng dụng lưu URL gốc vào bảng
 * `url_mappings`.
 * Cơ sở dữ liệu tự động gán một <b>ID số nguyên duy nhất</b> (kiểu `LONG`) cho
 * bản ghi mới này.
 * </li>
 * <li>
 * <strong>Mã hóa ID thành Short Code:</strong> Lấy ID vừa được DB cấp, sử dụng
 * thuật toán <b>mã hóa Base62</b>
 * để chuyển đổi ID số nguyên này thành một chuỗi ký tự ngắn gọn (ví dụ: ID
 * `1234567` thành mã `D9R`).
 * </li>
 * <li>
 * <strong>Cập nhật Short Code vào DB:</strong> Mã ngắn vừa tạo được cập nhật
 * lại vào chính bản ghi URL gốc trong cơ sở dữ liệu.
 * </li>
 * <li>
 * <strong>Trả về URL rút gọn:</strong> Ứng dụng kết hợp tên miền của dịch vụ
 * với mã ngắn (ví dụ: `https://yourdomain.com/D9R`) và trả về cho người dùng.
 * </li>
 * </ol>
 *
 * <br>
 *
 * ### 2. Quá trình truy cập URL rút gọn (Redirection Process)
 * Khi người dùng nhấp vào URL rút gọn đã được tạo (ví dụ:
 * `https://yourdomain.com/D9R`):
 * <ol>
 * <li>
 * <strong>Nhận Short Code:</strong> Ứng dụng nhận yêu cầu HTTP và trích xuất
 * <b>mã ngắn</b> (`D9R`) từ URL.
 * </li>
 * <li>
 * <strong>Giải mã Short Code thành ID:</strong> Sử dụng thuật toán <b>giải mã
 * Base62</b>, mã ngắn (`D9R`) được chuyển đổi ngược lại thành <b>ID số
 * nguyên</b> tương ứng (`1234567`).
 * </li>
 * <li>
 * <strong>Tra cứu URL gốc trong DB:</strong> Ứng dụng dùng ID này để <b>truy
 * vấn trực tiếp cơ sở dữ liệu theo khóa chính (Primary Key)</b> để tìm URL gốc.
 * </li>
 * <li>
 * <strong>Chuyển hướng (HTTP Redirect):</strong>
 * <ul>
 * <li>Nếu tìm thấy URL gốc, ứng dụng gửi một phản hồi <b>HTTP Redirect (301
 * hoặc 302)</b> tới trình duyệt của người dùng, yêu cầu trình duyệt chuyển
 * hướng đến URL gốc đó.</li>
 * <li>Nếu không tìm thấy, hệ thống trả về lỗi 404 Not Found.</li>
 * </ul>
 * </li>
 * </ol>
 */
public class SU {
    /**
     * Chuyển đổi một số nguyên (long) thành chuỗi Base62.
     * Thường dùng để chuyển đổi ID tự tăng từ cơ sở dữ liệu thành mã ngắn.
     *
     * @param number Số nguyên cần chuyển đổi (ví dụ: ID của bản ghi URL trong DB).
     * @return Chuỗi Base62 tương ứng.
     */
    private String encode(long number) {
        if (number == 0) {
            return String.valueOf(Constants.BASE62_CHARS.charAt(0)); // Trả về '0' nếu số là 0
        }

        StringBuilder sb = new StringBuilder();
        // Thực hiện phép chia lấy dư và chia nguyên liên tục
        while (number > 0) {
            int balance = (int) number % 62;
            sb.append(Constants.BASE62_CHARS.charAt(balance));
            number /= 62;
        }
        // Đảo ngược chuỗi vì chúng ta xây dựng từ phải sang trái
        return sb.reverse().toString();
    }

    /**
     * Chuyển đổi một chuỗi Base62 trở lại số nguyên (long).
     * Thường dùng để lấy ID từ mã ngắn khi người dùng truy cập.
     *
     * @param base62String Chuỗi Base62 cần chuyển đổi.
     * @return Số nguyên (long) tương ứng.
     * @throws IllegalArgumentException nếu chuỗi chứa ký tự không hợp lệ.
     */
    private long decode(String base62String) {
        long number = 0;
        int power = 0;
        // Duyệt chuỗi từ phải sang trái để tính toán giá trị đúng
        for (int i = base62String.length() - 1; i >= 0; i--) {
            char c = base62String.charAt(i);
            int charValue = Constants.BASE62_CHARS.indexOf(c); // Lấy giá trị thập phân của ký tự trong hệ cơ số 62
            if (charValue == -1) {
                throw new IllegalArgumentException("Ký tự không hợp lệ trong chuỗi Base62: " + c);
            }
            number += charValue * Math.pow(62, power); // Cộng dồn giá trị
            power++; // Tăng lũy thừa của 62
        }
        return number;
    }

    public static void main(String[] args) {
        SU converter = new SU();
        // 1. Giả sử bạn có một ID từ cơ sở dữ liệu (ví dụ: ID của bản ghi URL)
        long databaseId = 1234567L;
        // 2. Mã hóa ID đó thành mã ngắn Base62
        String shortCode = converter.encode(databaseId);
        System.out.println("ID cơ sở dữ liệu: " + databaseId);
        System.out.println("Mã ngắn Base62 tạo được: " + shortCode);
        // 3. (Khi người dùng truy cập mã ngắn) Giải mã mã ngắn để lấy lại ID
        // Bạn chỉ truyền MÃ NGẮN vào decode, không phải URL đầy đủ
        long decodedId = converter.decode(shortCode);
        System.out.println("Giải mã mã ngắn '" + shortCode + "' thành ID: " + decodedId);
    }
}
