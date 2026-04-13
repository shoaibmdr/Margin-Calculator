package com.margincalculator.dto;

/**
 * Status of a client's approved margin trading limit.
 */
public enum LimitStatus {
    /** Limit is valid and not near expiry. */
    ACTIVE,
    /** Limit is expiring within 30 days. */
    EXPIRING_SOON,
    /** Limit expiry date has passed. */
    EXPIRED
}
