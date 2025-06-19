package ua.edu.ukma.clientserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GoodsDao implements AutoCloseable {

    private static final String TABLE_NAME = "goods";

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String CATEGORY = "category";
    private static final String AMOUNT = "amount";
    private static final String PRICE = "price";

    private final Connection connection;

    public GoodsDao(Connection connection) {
        this.connection = connection;
    }

    public int create(Goods goods) {
        String sql = String.format(
            "INSERT INTO %s (%s, %s, %s, %s) VALUES (?, ?, ?, ?)",
            TABLE_NAME, NAME, CATEGORY, AMOUNT, PRICE
        );

        try (PreparedStatement query = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            query.setString(1, goods.getName());
            query.setString(2, goods.getCategory());
            query.setInt(3, goods.getAmount());
            query.setDouble(4, goods.getPrice());
            query.executeUpdate();

            ResultSet keys = query.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Goods goods) {
        String sql = String.format(
            "UPDATE %s SET %s = ?, %s = ?, %s = ?, %s = ? WHERE %s = ?",
            TABLE_NAME, NAME, CATEGORY, AMOUNT, PRICE, ID
        );

        try (PreparedStatement query = connection.prepareStatement(sql)) {
            query.setString(1, goods.getName());
            query.setString(2, goods.getCategory());
            query.setInt(3, goods.getAmount());
            query.setDouble(4, goods.getPrice());
            query.setInt(5, goods.getId());
            query.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLE_NAME, ID);

        try (PreparedStatement query = connection.prepareStatement(sql)) {
            query.setInt(1, id);
            query.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAll() {
        String sql = String.format("DELETE FROM %s", TABLE_NAME);

        try (PreparedStatement query = connection.prepareStatement(sql)) {
            query.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Goods getById(int id) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLE_NAME, ID);

        Goods goods = null;
        try (PreparedStatement query = connection.prepareStatement(sql)) {
            query.setInt(1, id);
            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {
                goods = extractGoodsFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return goods;
    }

    public List<Goods> getAll() {
        return getByParams(new SearchParams());
    }

    public List<Goods> getByParams(SearchParams params) {
        StringBuilder sql = new StringBuilder("SELECT * FROM " + TABLE_NAME);
        List<Object> arguments = new ArrayList<>();
        buildWhereClause(sql, arguments, params);

        List<Goods> goods = new ArrayList<>();
        try (PreparedStatement query = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < arguments.size(); i++) {
                query.setObject(i+1, arguments.get(i));
            }

            ResultSet resultSet = query.executeQuery();
            while (resultSet.next()) {
                goods.add(extractGoodsFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return goods;
    }

    private void buildWhereClause(StringBuilder sb, List<Object> arguments, SearchParams params) {
        ArrayList<String> conditions = new ArrayList<>();

        if (params.getName() != null && !params.getName().isBlank()) {
            conditions.add(NAME + " ILIKE ?");
            arguments.add("%" + params.getName() + "%");
        }

        if (params.getCategory() != null && !params.getCategory().isBlank()) {
            conditions.add(CATEGORY + " ILIKE ?");
            arguments.add("%" + params.getCategory() + "%");
        }

        if (params.getPriceFrom() != null) {
            conditions.add(PRICE + " >= ?");
            arguments.add(params.getPriceFrom());
        }

        if (params.getPriceTo() != null) {
            conditions.add(PRICE + " <= ?");
            arguments.add(params.getPriceTo());
        }

        if (params.getAmountFrom() != null) {
            conditions.add(AMOUNT + " >= ?");
            arguments.add(params.getAmountFrom());
        }

        if (params.getAmountTo() != null) {
            conditions.add(AMOUNT + " <= ?");
            arguments.add(params.getAmountTo());
        }

        if (!conditions.isEmpty()) {
            sb.append(" WHERE ");
            sb.append(String.join(" AND ", conditions));
        }
    }

    private static Goods extractGoodsFromResultSet(ResultSet resultSet) throws SQLException {
        return Goods.builder()
            .id(resultSet.getInt(ID))
            .name(resultSet.getString(NAME))
            .category(resultSet.getString(CATEGORY))
            .price(resultSet.getDouble(PRICE))
            .amount(resultSet.getInt(AMOUNT))
            .build();
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
