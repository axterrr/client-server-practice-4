package ua.edu.ukma.clientserver;

public class Main {

    public static void main(String[] args) {
        DaoFactory daoFactory = DaoFactory.getDaoFactory();
        try(GoodsDao goodsDao = daoFactory.goodsDao()) {
            System.out.println("OK");
        }
    }
}
