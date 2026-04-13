package com.margincalculator.dto;

/**
 * Margin status action based on current equity percentage.
 */
public enum MarginAction {

    /**
     * Current Equity % >= Initial Margin % – account is in a safe state.
     */
    HEALTHY,

    /**
     * Trigger Margin % <= Current Equity % < Maintenance Margin % –
     * client must deposit additional funds or securities to restore equity.
     */
    MARGIN_CALL,

    /**
     * Current Equity % < Trigger Margin % –
     * broker initiates forced liquidation to reduce exposure.
     */
    FORCE_SELL
}
