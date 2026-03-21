package com.billpayments.billpaymentsystem.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceProvider {

    //ELECTRICITY
    IKEJA_ELECTRIC("ikeja-electric", TransactionType.ELECTRICITY),
    EKO_ELECTRIC("eko-electric", TransactionType.ELECTRICITY),
    ABUJA_ELECTRIC("abuja-electric", TransactionType.ELECTRICITY),
    BENIN_ELECTRIC("benin-electric", TransactionType.ELECTRICITY),
    ENUGU_ELECTRIC("enugu-electric", TransactionType.ELECTRICITY),
    IBADAN_ELECTRIC("ibadan-electric", TransactionType.ELECTRICITY),
    JOS_ELECTRIC("jos-electric", TransactionType.ELECTRICITY),
    KADUNA_ELECTRIC("kaduna-electric", TransactionType.ELECTRICITY),
    KANO_ELECTRIC("kano-electric", TransactionType.ELECTRICITY),
    PORT_HARCOURT_ELECTRIC("phed", TransactionType.ELECTRICITY),

    // Airtime
    MTN("mtn", TransactionType.AIRTIME),
    GLO("glo", TransactionType.AIRTIME),
    AIRTEL("airtel", TransactionType.AIRTIME),
    ETISALAT("etisalat", TransactionType.AIRTIME),

    // Data
    MTN_DATA("mtn-data", TransactionType.DATA),
    GLO_DATA("glo-data", TransactionType.DATA),
    AIRTEL_DATA("airtel-data", TransactionType.DATA),
    ETISALAT_DATA("etisalat-data", TransactionType.DATA),

    // Cable TV
    DSTV("dstv", TransactionType.CABLE_TV),
    GOTV("gotv", TransactionType.CABLE_TV),
    STARTIMES("startimes", TransactionType.CABLE_TV);

    private final String vtpassServiceId;
    private final TransactionType transactionType;

    public static ServiceProvider fromServiceId(String serviceId) {
        for (ServiceProvider provider : values()) {
            if (provider.vtpassServiceId.equalsIgnoreCase(serviceId)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unsupported service: " + serviceId);
    }
}
