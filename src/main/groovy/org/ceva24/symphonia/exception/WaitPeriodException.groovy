package org.ceva24.symphonia.exception

class WaitPeriodException extends RuntimeException {

    Integer hoursRemaining

    WaitPeriodException(Integer hoursRemaining) {

        super('Cannot tweet during wait period')

        this.hoursRemaining = hoursRemaining
    }
}