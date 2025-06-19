package ua.edu.ukma.clientserver;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class GoodsDaoTest {

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13.4")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("schema.sql");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    private GoodsDao dao;

    @BeforeEach
    void setUp() throws SQLException {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(postgres.getJdbcUrl());
        ds.setUser(postgres.getUsername());
        ds.setPassword(postgres.getPassword());
        dao = new GoodsDao(ds.getConnection());
    }

    @AfterEach
    void tearDown() {
        dao.deleteAll();
        dao.close();
    }

    @Test
    void testCreate() {
        assertEquals(0, dao.getAll().size());
        dao.create(TestUtils.randomGoods());
        assertEquals(1, dao.getAll().size());
    }

    @Test
    void testGetById() {
        Goods goods = TestUtils.randomGoods();
        int id = dao.create(goods);
        goods.setId(id);
        assertEquals(goods, dao.getById(id));
    }

    @Test
    void testUpdate() {
        int id = dao.create(TestUtils.randomGoods());
        Goods updated = TestUtils.randomGoods();
        updated.setId(id);
        dao.update(updated);
        assertEquals(updated, dao.getById(id));
    }

    @Test
    void testDelete() {
        int id = dao.create(TestUtils.randomGoods());
        assertEquals(1, dao.getAll().size());
        dao.delete(id);
        assertEquals(0, dao.getAll().size());
    }

    @Test
    void testGetByParamsByPrice() {
        Goods goods1 = TestUtils.randomGoods();
        Goods goods2 = TestUtils.randomGoods();
        goods2.setPrice(goods1.getPrice() - 0.01);

        dao.create(goods1);
        dao.create(goods2);

        SearchParams params = SearchParams.builder()
            .priceFrom(goods1.getPrice())
            .build();
        assertEquals(1, dao.getByParams(params).size());
    }

    @Test
    void testGetByParamsByPriceNoResults() {
        Goods goods1 = TestUtils.randomGoods();
        Goods goods2 = TestUtils.randomGoods();
        goods2.setPrice(goods1.getPrice() - 0.01);

        dao.create(goods1);
        dao.create(goods2);

        SearchParams params = SearchParams.builder()
            .priceFrom(goods1.getPrice() + 0.01)
            .build();
        assertEquals(0, dao.getByParams(params).size());
    }

    @Test
    void testGetByParamsByPartOfNameIgnoringCase() {
        String name = TestUtils.randomString(10);
        String searchName = name.substring(3, 7);

        Goods goods1 = TestUtils.randomGoods();
        Goods goods2 = TestUtils.randomGoods();
        goods1.setName(name.toLowerCase());
        goods2.setName(name.toUpperCase());

        dao.create(goods1);
        dao.create(goods2);

        SearchParams params = SearchParams.builder()
                .name(searchName)
                .build();
        assertEquals(2, dao.getByParams(params).size());
    }
}
