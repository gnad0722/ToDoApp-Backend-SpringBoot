package dev.gnad.SpringModulith.common.utility.filter;

public record FilterCondition(
        String field,
        FilterOperator operator,
        String value
) {
}
