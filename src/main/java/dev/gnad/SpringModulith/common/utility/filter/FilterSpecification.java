package dev.gnad.SpringModulith.common.utility.filter;

import org.springframework.data.jpa.domain.Specification;


//This class is used to create specification query follow field of object
public final class FilterSpecification {
    private  FilterSpecification(){}



    public static <T>Specification<T> equal(
            String field,
            Object value
    ){
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(field), value)
        );
    }


    public static <T>Specification<T> contains(
            String field,
            String value
    ){
        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get(field)),
                        "%" + value.toLowerCase() + "%"
                );
    }
}
