package dev.gnad.SpringModulith.common.utility.filter;

public enum FilterOperator {
    EQ,          // =
    NOT_EQ,      // !=
    CONTAINS,    // LIKE %value%
    STARTS_WITH, // LIKE value%
    ENDS_WITH,   // LIKE %value
    GT,          // >
    GTE,         // >=
    LT,          // <
    LTE,         // <=
    IS_NULL,
    IS_NOT_NULL
}