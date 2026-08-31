package io.littlehorse.examples;

import io.littlehorse.sdk.common.proto.InlineStruct;
import io.littlehorse.sdk.worker.LHType;
import io.littlehorse.sdk.worker.LHTaskMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StructTasks {

    private static final Logger log = LoggerFactory.getLogger(StructTasks.class);

    @LHTaskMethod("lookup-address")
    public Address lookupAddress(String customerId) {
        log.info("Looking up an address for {}", customerId);
        return new Address("124 Sand Dune Lane", "Anchorhead", "Tatooine", 97412);
    }

    @LHTaskMethod("normalize-address")
    @LHType(structDefName = "address")
    public InlineStruct normalizeAddress(
            @LHType(structDefName = "address") InlineStruct address) {
        log.info("Normalizing inline address {}", address);
        return address;
    }

    @LHTaskMethod("audit-customer")
    public String auditCustomer(String displayName, String city) {
        log.info("Auditing customer {} in {}", displayName, city);
        return "audited";
    }

    @LHTaskMethod("save-order")
    public String saveOrder(PurchaseOrder order) {
        log.info("Saving {}", order);
        return order.getOrderId();
    }
}
