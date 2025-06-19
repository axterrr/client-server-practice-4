package ua.edu.ukma.clientserver;

import java.util.Random;

public class TestUtils {

    static final Random random = new Random();

    static Goods randomGoods() {
        return Goods.builder()
            .id(random.nextInt())
            .name(randomString(10))
            .category(randomString(10))
            .amount(random.nextInt())
            .price(random.nextDouble())
            .build();
    }

    static String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append((char) (Math.random() * 26 + 'a'));
        }
        return sb.toString();
    }
}
