package ru.antoncharov.socks.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.antoncharov.socks.domain.Socks;

public class SocksSpecification {

    public static Specification<Socks> toPredicate(String operation, Integer value) {
        if (operation.equalsIgnoreCase("moreThan")) {
            return (root, query, builder) ->
                builder.greaterThanOrEqualTo(root.get("quantity"), value);
        }
        if (operation.equalsIgnoreCase("lessThan")) {
            return (root, query, builder) ->
                    builder.lessThanOrEqualTo(root.get("quantity"), value);
        }
        if (operation.equalsIgnoreCase("equal")) {
            return (root, query, builder) ->
                    builder.equal(root.get("quantity"), value);
        }

        throw new RuntimeException("Unknown operation: " + operation);
    }

    public static Specification<Socks> byColor(String color) {
        return (root, query, builder) -> builder.equal(root.get("color"), color);
    }
}
