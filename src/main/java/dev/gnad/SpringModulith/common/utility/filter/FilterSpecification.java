package dev.gnad.SpringModulith.common.utility.filter;

import dev.gnad.SpringModulith.common.api.exception.BadRequestException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;


//This class is used to create specification query follow field of object
public final class FilterSpecification {

    private  FilterSpecification(){}

    public static List<FilterCondition> parseFilters(List<String> filters) {
        if (filters == null) {
            return List.of();
        }

        return filters.stream()
                .map(raw -> {
                    String[] parts = raw.split(":", 3);

                    if (parts.length != 3) {
                        throw invalidFilter(raw);
                    }

                    try {
                        return new FilterCondition(
                                parts[0],
                                FilterOperator.valueOf(
                                        parts[1].toUpperCase(Locale.ROOT)),
                                parts[2]
                        );
                    } catch (IllegalArgumentException ex) {
                        throw invalidFilter(raw);
                    }
                })
                .toList();
    }

    public static <T> Specification<T> build(
            List<FilterCondition> filters,
            Set<String> allowedFields) {

        Specification<T> result = Specification.allOf(List.of());

        if (filters == null || filters.isEmpty()) {
            return result;
        }

        for (FilterCondition filter : filters) {
            validateField(filter, allowedFields);

            Specification<T> condition =
                    createSpecification(filter);

            result = result.and(condition);
        }
        return result;
    }

    private static void validateField(
            FilterCondition filter,
            Set<String> allowedFields) {

        if (filter.field() == null
                || !allowedFields.contains(filter.field())) {
            throw new BadRequestException(
                    "Invalid filter: " + filter.field()
            );
        }

        if (filter.operator() == null) {
            throw invalidFilter(filter);
        }
    }

    public static <T> Specification<T> belongsTo(
            Integer id,
            String objectName) {

        return (root, query, cb) ->
                cb.equal(root.get(objectName).get("id"), id);
    }

    private static <T> Specification<T> createSpecification(
            FilterCondition filter) {

        return (root, query, cb) -> {
            try {
                return toPredicate(filter, root, cb);
            } catch (IllegalArgumentException | DateTimeException ex) {
                // Bad value for the field type (number, boolean, date-time, enum, ...)
                throw invalidFilter(filter);
            }
        };
    }

    private static BadRequestException invalidFilter(String raw) {
        return new BadRequestException("Invalid filter: " + raw);
    }

    private static BadRequestException invalidFilter(FilterCondition filter) {
        String operator = filter.operator() == null
                ? ""
                : filter.operator().name().toLowerCase(Locale.ROOT);
        return invalidFilter(
                filter.field() + ":" + operator + ":"
                        + (filter.value() == null ? "" : filter.value())
        );
    }

    private static <T> Predicate toPredicate(
            FilterCondition filter,
            Root<T> root,
            CriteriaBuilder cb) {

            Path<?> path = root.get(filter.field());
            FilterOperator operator = filter.operator();
            String value = filter.value();
            return switch (operator) {
                case IS_NULL -> cb.isNull(path);

                case IS_NOT_NULL -> cb.isNotNull(path);

                case EQ -> cb.equal(
                        path, convert(value, path.getJavaType()));


                case NOT_EQ -> cb.notEqual(
                        path, convert(value, path.getJavaType()));

                case CONTAINS -> cb.like(
                        cb.lower(root.get(filter.field()).as(String.class)),
                        "%" + escapeLike(value.toLowerCase()) + "%",
                        '\\'
                );

                case STARTS_WITH -> cb.like(
                        cb.lower(root.get(filter.field()).as(String.class)),
                        escapeLike(value.toLowerCase()) + "%",
                        '\\'
                );

                case ENDS_WITH -> cb.like(
                        cb.lower(root.get(filter.field()).as(String.class)),
                        "%" + escapeLike(value.toLowerCase()),
                        '\\'
                );

                case GT -> compare(path, value, operator, cb);

                case GTE -> compare(path, value, operator, cb);

                case LT -> compare(path, value, operator, cb);

                case LTE -> compare(path, value, operator, cb);
            };
    }

    private static Object convert(String value, Class<?> type) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "This operator requires a value");
        }

        if (type == String.class) return value;
        if (type == Integer.class || type == int.class)
            return Integer.valueOf(value);
        if (type == Long.class || type == long.class)
            return Long.valueOf(value);
        if (type == Boolean.class || type == boolean.class)
            return parseBoolean(value);
        if (type == LocalDateTime.class)
            return LocalDateTime.parse(value);
        if (type.isEnum()) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Object result = Enum.valueOf(
                    (Class<? extends Enum>) type.asSubclass(Enum.class),
                    value
            );
            return result;
        }

        throw new IllegalArgumentException(
                "Unsupported filter type: " + type.getSimpleName());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Predicate compare(
            Path<?> path,
            String value,
            FilterOperator operator,
            CriteriaBuilder cb) {

        Comparable converted = (Comparable) convert(
                value, path.getJavaType());

        Expression<? extends Comparable> expression =
                (Expression<? extends Comparable>) path;

        return switch (operator) {
            case GT -> cb.greaterThan(expression, converted);
            case GTE -> cb.greaterThanOrEqualTo(expression, converted);
            case LT -> cb.lessThan(expression, converted);
            case LTE -> cb.lessThanOrEqualTo(expression, converted);
            default -> throw new IllegalArgumentException(
                    "Unsupported comparison operator: " + operator);
        };
    }

    private static Boolean parseBoolean(String value) {
        if ("true".equalsIgnoreCase(value)) return true;
        if ("false".equalsIgnoreCase(value)) return false;
        throw new IllegalArgumentException("Invalid boolean: " + value);
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
