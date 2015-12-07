package org.ceva24.symphonia.exception

class WaitPeriodException extends RuntimeException {

    Integer secondsRemaining

    WaitPeriodException(Integer secondsRemaining) {

        super('Cannot tweet during wait period')

        this.secondsRemaining = secondsRemaining
    }
}