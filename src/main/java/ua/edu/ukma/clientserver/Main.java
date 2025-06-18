package ua.edu.ukma.clientserver;

public class Main {

    public static void main(String[] args) {
        DaoFactory daoFactory = DaoFactory.getDaoFactory();
        try(GoodsDao goodsDao = daoFactory.goodsDao()) {
            //goodsDao.create(new Goods(0, "name", "category", 10, 100.0));
            SearchParams params = SearchParams.builder()
                .name("AM")
                .build();
            System.out.println(goodsDao.getByParams(params));
        }
    }
}
